import io.practice.handlers.PageHandler
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.PrivateMethodTester

class PageHandlerTest extends AnyFunSuite with PrivateMethodTester {
  test("PageHandler.convertStringToRu") {
    val stopList = Set("хакер", "взлом", "ddos", "доход", "paypal", "фрибет")
    val whiteList = Set("notSoBad")
    val pageHandler = new PageHandler(whiteList, stopList)
    val convertStringToRu = PrivateMethod[List[String]](Symbol("convertStringToRu"))

    var list = pageHandler invokePrivate convertStringToRu("hakеp")
    assert(list.size === 4)
    assert(list === List("накер","накеп", "хакер" ,"хакеп"))

    list = pageHandler invokePrivate convertStringToRu("xakеr")
    assert(list.size === 1)
    assert(list === List("хакер"))

    list = pageHandler invokePrivate convertStringToRu("фpиbеt")
    assert(list.size === 4)
    assert(list === List("фривет","фрибет", "фпивет" ,"фпибет"))

    list = pageHandler invokePrivate convertStringToRu("взлоm")
    assert(list.size === 2)
    assert(list === List("взлом", "взлот"))
  }

  test("PageHandler.findStopWords") {
    val stopList = Set("хакер", "взлом", "ddos", "доход", "paypal", "фрибет")
    val whiteList = Set("notSoBad")
    val pageHandler = new PageHandler(whiteList, stopList)
    val findStopWords = PrivateMethod[List[String]](Symbol("findStopWords"))

    var list = pageHandler invokePrivate findStopWords("hakеp")
    assert(list === List("хакер"))

    list = pageHandler invokePrivate findStopWords("xakеr")
    assert(list === List("хакер"))

    list = pageHandler invokePrivate findStopWords("услуги хaкерa, взлoм социальных сетей")
    assert(list === List("взлом"))

    list = pageHandler invokePrivate findStopWords("Bзлom сoциальных сeтeй")
    assert(list === List("взлом"))

    list = pageHandler invokePrivate findStopWords("забирай фриbet на евро")
    assert(list === List("фрибет"))

    list = pageHandler invokePrivate findStopWords("забирай фрибem на евро")
    assert(list === List("фрибет"))

    list = pageHandler invokePrivate findStopWords("DОХОD В НЕDЕЛЮ ОТ 1000$")
    assert(list === List("доход"))
  }

}
