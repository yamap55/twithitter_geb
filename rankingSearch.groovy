@Grapes([
    @Grab("org.gebish:geb-core:1.1.1"),
    @Grab("org.seleniumhq.selenium:selenium-support:3.4.0"),
    @Grab("org.codehaus.groovy.modules.http-builder:http-builder:0.7.1"),
])

import twithitter.page.*
import twithitter.*

import geb.Browser

println "start ${new Date().format('yyyy/MM/dd HH:mm:ss')}"


def f = {
  Browser.drive() {
    to LoginPage
    login(browser.config.rawConfig.loginTwitterId, browser.config.rawConfig.loginTwitterPassword)

    waitFor{ title == "TwitHitter" }

    to RankingPage
    waitFor { $(".teams") }
    // 初期表示されているAリーグの1ページ目のチームデータ取得
    def teamData = getTeamData()

  }
}
f()

println "end ${new Date().format('yyyy/MM/dd HH:mm:ss')}"
