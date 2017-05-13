package twithitter.page
import geb.Page

class ScoutPage extends Page {
  static url = "scout"
  static at = { waitFor { ($("h2").first().text() == "選手スカウト") && $(".search-random") && $(".search-result") } }
  static content = {
    randomSearchButton { $(".search-random") }
    passwordField { $("#password") }
    loginButton { $(".submit").first() }
  }

  def randomSearch() {
    randomSearchButton.click()
    waitFor { $(".player") }
  }

  def getPlayerIDs() {
    $(".player .screen-name")*.text().collect{it - "@"}
  }
}
