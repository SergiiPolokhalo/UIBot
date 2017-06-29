package bot

import scalatags.JsDom.all._
import org.scalajs.dom

import BotApp.{ showStep, container, rmBtn }

object HtmlFabric {
  def btnClass(p: Map[String, String]): String = {
    p.getOrElse(UserButton.ACTION, "") match {
      case UserButton.TO_STEP => "btn btn-default"
      case UserButton.TO_URL => "btn btn-info"
      case UserButton.TO_INPUT => "btn btn-success"
      case _ => "btn btn-default"
    }
  }
  def btnFn(p: Map[String, String], txt: String = ""): () => String = {
    p.getOrElse(UserButton.ACTION, "") match {
      case UserButton.TO_STEP => () => {
        showStep(
          p.getOrElse(UserButton.ID, "0"),
          Some(HtmlFabric(UserMessage(txt, p))))
        ""
      } //goto next step
      case UserButton.TO_URL => () => {
        val url = p.getOrElse(UserButton.URL, "")
        if ("" != url) {
          dom.window.location.replace(url)
        }
        ""
      } //goto url
      case UserButton.TO_INPUT => () => {
        import BotApp.rmBtn
        rmBtn()
        container.appendChild(HtmlFabric(UserInput(txt, p)))
        //TODO clear buttons
        //add element as input over BotApp
        ""
      } //clear buttons and switch to input
      case _ => () => ""
    }
  }

  def apply(r: Render): dom.html.Element = {
    val res = r match {
      case SysMessage(txt) => div(cls := "alert alert-info", role := "alert",
        span(txt)).render

      case UserButton(txt, params) => {
        val button = input(value := txt, `type` := "button", cls := btnClass(params)).render
        button.onclick = (e: dom.Event) => { btnFn(params, txt)() }
        button
      }

      case UserMessage(txt, params) => div(cls := "alert alert-success", role := "alert",
        span(txt)).render

      case UserInput(txt, params) => {
        val p = params + (UserButton.ACTION -> UserButton.TO_STEP)
        val inp = input(placeholder := txt, `type` := "text", cls := "form-input").render
        inp.onkeyup = (e: dom.KeyboardEvent) => {
          if (e.keyCode == 13) {
            val msg = UserMessage(inp.value, p)
            showStep(p.getOrElse(UserButton.ID, ChatPlugin.initialStep), Some(HtmlFabric(msg)))
          }
        }
        inp
      }

      case Chat(ident, messages, responses) => {
        val chat_box = div(id := "__"+ident,
          messages.map(HtmlFabric(_)),
          responses.map(HtmlFabric(_))).render
        chat_box
      }

      case ChatHolder(chats) => div(id := "chat_holder", chats.map { x => HtmlFabric(x) }).render

      case _ => div().render
    }
    res
  }

  private def mkButt(txt: String, c: String = "") = input(value := txt,
    `type` := "button", cls := c).render
  private def mkInp(txt: String, c: String = "") = input(`type` := "text",
    placeholder := txt).render
}