package twithitter.page
import geb.Page

class RankingPage extends Page {
  static url = "ranking"
  static at = { waitFor { ($("h2").first().text() == "ランキング") } }
  static content = {
    leagueList { $("ul[data-tab_type='league']").find("li") }
  }

  // 表示されているページのチームデータを取得
  def getTeamData() {
    def teams = $(".teams")[0].find("a").collect {
      [name:it.text(), id:it.attr("href").split("/").last()]
    }
    def records = $(".teams")[1].find("tbody tr").collect{it.find("td")*.text()}
    assert teams.size() == records.size()

    teams.eachWithIndex {it, i ->
      it["record"] = records[i]
    }
    teams
  }
}
