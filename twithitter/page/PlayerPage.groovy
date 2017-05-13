package twithitter.page
import geb.Page

class PlayerPage extends Page {
  static url = ""
  static at = { waitFor { title == "TwitHitter" } }
  static content = {
    statusArea { ($(".batter-status") ?: $(".pitcher-status")).first() }
    playerProfile { $(".player-profile").first() }
  }

  String convertToPath(id) {
    "${id}"
  }

  def existsPlayer() {
    $(".player-profile") && ($(".batter-status") || $(".pitcher-status"))
  }

  def getPleyerType() {
    $(".batter-status") ? "打者" : "投手"
  }
}
