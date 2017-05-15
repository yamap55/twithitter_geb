@Grapes([
    @Grab("org.gebish:geb-core:1.1.1"),
    @Grab("org.seleniumhq.selenium:selenium-support:3.4.0"),
    @Grab("org.codehaus.groovy.modules.http-builder:http-builder:0.7.1"),
])

import twithitter.page.*
import twithitter.*

import groovyx.net.http.HTTPBuilder
import geb.Browser
import static groovyx.net.http.ContentType.*

def batterResultPath = "./result/batter.csv"
def pitcherResultPath = "./result/pitcher.csv"

println "start ${new Date().format('yyyy/MM/dd HH:mm:ss')}"

def batterResultFile = new File(batterResultPath)
def pitcherResultFile = new File(pitcherResultPath)

// 出力関数（出力対象の場合に呼ばれる）
def output = {user, googleWebAplicationId ->
  if (googleWebAplicationId) {
    def http = new HTTPBuilder( "https://script.google.com/macros/s/${googleWebAplicationId}/" )
    http.post( path: 'exec', body: user.asJson(),
               contentType: JSON ) { resp ->

      println "POST Success: ${resp.statusLine}"
    }
  } else {
    def result = user.isBatter() ? batterResultFile : pitcherResultFile
    result << "${user.asCsvString()}\n"
  }
}

def num = 100000

def createRandomStr = {
  def array = (0..9)+('a'..'z')+'_'
  def f = {
    new Random().with { (1..4).collect { array[nextInt(array.size())] }.join() }
  }
}()

def u = "https://twithitter.com/"
def errorCount = 0

def playerCount = 0
def prinlntCount = 0
def f = {
  Browser.drive() {
    def googleWebAplicationId = browser.config.rawConfig.googleWebAplicationId
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
        randomSearch()
        // sleep(500) // TODO 確認
        getPlayerIDs().each {
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
          if (user.status.isTarget()) {
            // 出力対象の場合
            def r = "${user.twitterId} : ${user.type} : ${user.status}"
            println r
            output(user, googleWebAplicationId)
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
