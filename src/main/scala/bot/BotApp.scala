package bot

import scalatags.JsDom.all._

import org.scalajs.dom

import scala.scalajs.js.JSApp
import upickle.default._
import org.scalajs.dom.experimental.domparser.DOMParser
import javax.swing.text.html.HTML
import org.scalajs.dom.raw.HTMLElement

object BotApp extends JSApp {

  import ChatPlugin.conv

  def getElem[T](ident: String): T = {
    val c = dom.document.getElementById(ident).asInstanceOf[T]
    if (c == null) {
      val d = div(id := ident).render
      dom.document.body.appendChild(d)
      getElem[T](ident)
    } else c
  }

  //if we have panel - use it if not - create new
  
  lazy val container = getElem[dom.html.Div]("botPanel")

  def showStep(step_id: String, addElem: Option[dom.html.Element] = None): Unit = {
    rmBtn()
    val beg = conv.chats.filter { x => x.ident == step_id }
    if (beg.nonEmpty) {
      addElem match {
        case Some(elem) => container.appendChild(elem)
        case None => {}
      }
      container.appendChild(HtmlFabric(beg.head))
    }
    //scroll down container
    container.scrollTop = container.scrollHeight
    //scroll page to anchor
    val t = getElem[HTMLElement](step_id).scrollIntoView(true);
    
    
  }

  def rmBtn() = {
    val inputs = container.getElementsByTagName("input")
    val tags = for (i <- 0 to inputs.length if inputs.item(i) != null) yield inputs.item(i)
    tags.foreach { x => x.parentNode.removeChild(x) }
  }

  def main(): Unit = {
    println(ChatPlugin.initialStep)
    p()
  }
  
  def p() = {
  import dom.ext.Ajax
  import scala.concurrent
              .ExecutionContext
              .Implicits
              .global
      import upickle.default._
    import upickle._            
  val url = dom.window.location.protocol+"/conversation.json"
  Ajax.get(url).onSuccess { case xhr =>    
    ChatPlugin.conv = read[ChatHolder](xhr.responseText)
    showStep(ChatPlugin.initialStep)
  }
}
}

//define classes
trait Render
case class SysMessage(txt: String) extends Render
case class UserButton(txt: String, params: Map[String, String]) extends Render
object UserButton {
  val ACTION = "action"
  val TO_STEP = "to_step"
  val TO_URL = "to_url"
  val TO_INPUT = "to_input"
  val ID = "next_step"
  val URL = "url"
}
case class UserInput(txt: String, params: Map[String, String]) extends Render
case class UserMessage(txt: String, params: Map[String, String]) extends Render
case class Chat(ident: String, messages: List[SysMessage], responses: List[UserButton]) extends Render
case class ChatHolder(chats: List[Chat]) extends Render

object ChatPlugin {
  import upickle.default._
  import upickle._

 
  var conv = ChatHolder(List(
    Chat("wrapper",
      List(SysMessage("Hi!"), SysMessage("How are You?")),
      List(
        UserButton("NISL BLANDIT", Map(UserButton.ACTION -> UserButton.TO_STEP, UserButton.ID -> "columns05")),
        UserButton("SED FEUGIAT", Map(UserButton.ACTION -> UserButton.TO_STEP, UserButton.ID -> "columns02")))),
    Chat("columns05",
      List(SysMessage("Do You want some Google?"), SysMessage("Or maybe back?")),
      List(
        UserButton("MAGNA ETIAM", Map(UserButton.ACTION -> UserButton.TO_STEP, UserButton.ID -> "columns01")),
        UserButton("TITLE", Map(UserButton.ACTION -> UserButton.TO_STEP, UserButton.ID -> "wrapper")))),
    Chat("columns01",
      List(SysMessage("Tell me, how many times You are smiling today?")),
      List(
        UserButton("SED FEUGIAT", Map(UserButton.ACTION -> UserButton.TO_STEP, UserButton.ID -> "columns02")),
        UserButton("NISL BLANDIT", Map(UserButton.ACTION -> UserButton.TO_STEP, UserButton.ID -> "columns05")))),
    Chat("columns02",
      List(SysMessage("Tell me, how many times You are smiling today?")),
      List(
        UserButton("SED FEUGIAT", Map(UserButton.ACTION -> UserButton.TO_STEP, UserButton.ID -> "columns01")),
        UserButton("NISL BLANDIT", Map(UserButton.ACTION -> UserButton.TO_STEP, UserButton.ID -> "wrapper"))))    
        ))
        
   def initialStep = conv.chats.head.ident
   
   println(write(conv))
}


