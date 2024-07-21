package web

import org.reactivestreams.{Subscriber, Subscription}
import org.slf4j.LoggerFactory
import scalikejdbc.streams._
import ujson.Obj
import web.Web.logger

import java.sql.Date
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global

class Subs[T](filter: T => Boolean, maxSize: Int = 1000, step: Int = 200000) extends Subscriber[T] {
  val res = ArrayBuffer[T]()
  var isDone = false

  override def onSubscribe(s: Subscription): Unit = {
    s.request(step)
  }

  override def onNext(t: T): Unit = {
    if (!this.isDone && res.size < maxSize) {
      if (filter(t)) {
        res.append(t)
      }
    }
  }

  override def onError(t: Throwable): Unit = {
    logger.error(t.getMessage)
  }

  override def onComplete(): Unit = {
    logger.debug("Done")
    isDone = true
  }
}

case class TgPost(channel: String, postDate: Date, id: Int, text: String, link: String, views: String) {
  def toJson: Obj = {
    ujson.Obj(
      "channel" -> channel,
      "post_date" -> postDate.toString,
      "id" -> id,
      "text" -> text,
      "link" -> link,
      "views" -> views
    )
  }
}

object CH extends App {

  val logger = LoggerFactory.getLogger(getClass)

  import scalikejdbc._

  ConnectionPool.singleton("jdbc:clickhouse://localhost:8123", "", "")

  def init() = {
    logger.debug("Pool initialized")
  }

  def tg(date: String, exp: Expression): Vector[TgPost] = {
    val dbIter: DatabasePublisher[TgPost] = DB readOnlyStream {
      sql"select channel, post_date, id, text, link, views from tg_channels where post_date=${date}".map(rs => {
        val channel = rs.string("channel")
        val postDate: Date = rs.date("post_date")
        val id = rs.int("id")
        val text = rs.string("text")
        val link = rs.string("link")
        val views = rs.string("views")

        TgPost(channel, postDate, id, text, link, views)
      }
      ).iterator()
    }

    val subs = new Subs[TgPost](post => Matcher.matchedText(post.text, exp))

    dbIter.subscribe(subs)

    subs.res.toVector
  }

  def tgEager(date: String, exp: Expression): Vector[TgPost] = {
    val fromDb = DB readOnly { implicit sess =>
      sql"select channel, post_date, id, text, link, views from tg_channels where post_date=${date}".map(rs => {
        val channel = rs.string("channel")
        val postDate: Date = rs.date("post_date")
        val id = rs.int("id")
        val text = rs.string("text")
        val link = rs.string("link")
        val views = rs.string("views")

        TgPost(channel, postDate, id, text, link, views)
      }
      ).toIterable.apply()
    }

    fromDb.filter(post => Matcher.matchedText(post.text, exp)).toVector
  }

  def tgChannel(): Vector[TgPost] = {
    val dbIter = DB readOnlyStream {
      sql"select channel, post_date, id, text, link, views from tg_channels where channel='ofnews' limit 1".map(rs => {
        val channel = rs.string("channel")
        val postDate: Date = rs.date("post_date")
        val id = rs.int("id")
        val text = rs.string("text")
        val link = rs.string("link")
        val views = rs.string("views")

        TgPost(channel, postDate, id, text, link, views)
      }
      ).iterator()
    }

    val subs = new Subs[TgPost](post => true)

    dbIter.subscribe(subs)

    subs.res.toVector
  }

  def tgChannelEager(): Vector[TgPost] = {
    val dbIter = DB readOnly { implicit session =>
      sql"select channel, post_date, id, text, link, views from tg_channels where channel='ofnews' limit 1".map(rs => {
        val channel = rs.string("channel")
        val postDate: Date = rs.date("post_date")
        val id = rs.int("id")
        val text = rs.string("text")
        val link = rs.string("link")
        val views = rs.string("views")

        TgPost(channel, postDate, id, text, link, views)
      }
      ).toIterable.apply()
    }

    dbIter.toVector
  }

  logger.debug(tgChannelEager().toString())
}
