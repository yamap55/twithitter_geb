package twithitter.page
import geb.Page

class Player extends Page {
  static url = "https://twithitter.com"
  static at = { waitFor { $(".player-profile") } }
  static content = {
    statusArea { ($(".batter-status") ?: $(".pitcher-status")).first() }
    playerProfile { $(".player-profile").first() }
  }

  String convertToPath(id) {
    "/${id}"
  }

  def existsPlayer() {
    $(".batter-status") || $(".pitcher-status")
  }

  def getPleyerType() {
    $(".batter-status") ? "打者" : "投手"
  }
}
