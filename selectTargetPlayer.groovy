@Grapes([
    @Grab("org.gebish:geb-core:1.1.1"),
    @Grab("org.seleniumhq.selenium:selenium-support:3.4.0"),
    @Grab("org.codehaus.groovy.modules.http-builder:http-builder:0.7.1"),
])

import twithitter.page.*
import twithitter.*

import geb.Browser

def batterResultPath = "./result/batter.csv"
def pitcherResultPath = "./result/pitcher.csv"

println "start ${new Date().format('yyyy/MM/dd HH:mm:ss')}"

def batterResultFile = new File(batterResultPath)
def pitcherResultFile = new File(pitcherResultPath)

def num = 100000

def errorCount = 0

def playerCount = 0
def prinlntCount = 0
def outputPlayerIds = []

def f = {
  Browser.drive() {
    def googleWebAplicationId = browser.config.rawConfig.googleWebAplicationId
    def outputer = new Outputer(googleWebAplicationId, batterResultFile, pitcherResultFile)
    to LoginPage
    login(browser.config.rawConfig.loginTwitterId, browser.config.rawConfig.loginTwitterPassword)

    waitFor{ title == "TwitHitter" }

    while(num > playerCount) {
      try {
        if (prinlntCount <= playerCount) {
          prinlntCount += 100
          println "${new Date().format('yyyy/MM/dd HH:mm:ss')}, ${playerCount}/${num}, error : ${errorCount}"
        }

        // スカウトのランダムページからIDを取得
        to ScoutPage
        def playerIds = getPlayerIDs(100)
        
        playerIds = playerIds - ["zoukin10", "Nefachel", "88957"]
        playerIds.each {
          playerCount++

          // 取得したIDを元に、Playerのデータを取得
          to PlayerPage, it
          if(!existsPlayer()) {
            // 存在しないIDの場合
            errorCount++
            println "errorid : ${it}"
            return
          }

          def pleyerType = getPleyerType()
          def user = new Player(playerProfile, pleyerType, statusArea)
          if (user.status.isTarget() && !outputPlayerIds.contains(user.twitterId)) {
            // 出力対象の場合
            outputPlayerIds << user.twitterId
            def r = "${user.twitterId} : ${user.type} : ${user.status}"
            println r
            outputer.output(user)
          }
        }
      } catch(e) {
        println "${new Date().format('yyyy/MM/dd HH:mm:ss')} exception !!!"
        println e.printStackTrace()
        report "exception_${new Date().format('yyyyMMddHHmmss')}"
      }
    }
  }
}

while (num > playerCount){
  try {
    f()
  } catch(e) {
    println "${new Date().format('yyyy/MM/dd HH:mm:ss')} exception !!"
    println e.printStackTrace()
  }
}

println "end ${new Date().format('yyyy/MM/dd HH:mm:ss')}"
