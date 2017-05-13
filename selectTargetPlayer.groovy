@Grapes([
    @Grab("org.gebish:geb-core:1.1.1"),
    @Grab("org.seleniumhq.selenium:selenium-support:3.4.0"),
    @Grab("org.codehaus.groovy.modules.http-builder:http-builder:0.7.1"),
])

import twithitter.page.*

import groovyx.net.http.HTTPBuilder
import groovy.json.JsonBuilder
import geb.Browser
import groovy.transform.ToString
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
  Browser.drive {
    def googleWebAplicationId = browser.config.rawConfig.googleWebAplicationId
    to Login
    login(browser.config.rawConfig.loginTwitterId, browser.config.rawConfig.loginTwitterPassword)

    waitFor{ title == "TwitHitter" }

    while(num > playerCount) {
      if (prinlntCount <= playerCount) {
        prinlntCount += 100
        println "${new Date().format('yyyy/MM/dd HH:mm:ss')}, ${playerCount}/${num}, error : ${errorCount}"
      }

      // スカウトのランダムページからIDを取得
      to Scout
      randomSearch()
      // sleep(500) // TODO 確認
      getPlayerIDs().each {
        playerCount++

        // 取得したIDを元に、Playerのデータを取得
        to Player, it
        if(!existsPlayer()) {
          // 存在しないIDの場合
          errorCount++
          println "errorid : ${it}"
          return
        }

        def pleyerType = getPleyerType()
        def user = new User(playerProfile, pleyerType, statusArea)

        if (user.status.isTarget()) {
          // 出力対象の場合
          def r = "${user.twitterId} : ${user.type} : ${user.status}"
          println r
          output(user, googleWebAplicationId)
        }
      }
    }
  }
}

while (num > playerCount){
  try {
    f()
  } catch(e) {
    println "${new Date().format('yyyy/MM/dd HH:mm:ss')} exception !!"
    println e
  }
}

println "end ${new Date().format('yyyy/MM/dd HH:mm:ss')}"

class User {
  def profileArea
  def name
  def twitterId
  def throwBat
  def team
  def popularity

  def type
  def status

  def getSimpleStatus() {
    isBatter()? status[0..4] : status[0..2]
  }

  def isPitcher() {
    type == "投手"
  }

  def isBatter() {
    type == "打者"
  }

  User(profileArea, playerType, statusArea) {
    def f = {
      profileArea.$(".${it}")[0].text()
    }
    name = f("name")
    twitterId = f("screen-name")
    throwBat = f("throw-bat")
    team = f("userteam-none-label")
    popularity = profileArea.find(".userteam-popularity")[0].text().split(" ")[1]

    type = playerType
    if (type == "打者") {
      // 打者の場合
      def s = statusArea.find(".left-column")[0].find(".status-val")*.text()
      status = new Status(type, s)
    } else {
      // 投手の場合
      def s = statusArea.find(".status-val")*.text()
      status = new Status(type, s)
    }
  }

  def asCsvString() {
    [twitterId,"",type,status.valueList].flatten().join(",")
  }

  def asJson() {
    def body = [
      id: twitterId,
      type: isPitcher() ? 0 : 1,
      status : [:]
    ]
    if (isPitcher()) {
      body.status["speed"] = status.value["球速"]
      body.status["control"] = status.value["制球"]
      body.status["stamina"] = status.value["スタミナ"]
      body.status["breaking_ball"] = status.value["変化"]
    } else {
      body.status["meet"] = status.value["ミート"]
      body.status["power"] = status.value["パワー"]
      body.status["run"] = status.value["走力"]
      body.status["arm"] = status.value["肩力"]
      body.status["def"] = status.valueList[4..status.valueList.size()-1]
    }
    new JsonBuilder(body).toString()
  }
}

@ToString(excludes = ["type", "valueList"])
class Status {
  def value = [:]
  def type
  def valueList

  Status(t, status) {
    type = t
    if (type == "打者") {
      valueList = status.collect{it as int}
      value["ミート"] = status[0] as int
      value["パワー"] = status[1] as int
      value["走力"] = status[2] as int
      value["肩力"] = status[3] as int
      value["捕"] = status[4] as int
      value["一"] = status[5] as int
      value["二"] = status[6] as int
      value["三"] = status[7] as int
      value["遊"] = status[8] as int
      value["左"] = status[9] as int
      value["中"] = status[10] as int
      value["右"] = status[11] as int
    } else {
      value["球速"] = status[0].split(" ")[0] as int
      value["制球"] = status[1] as int
      value["スタミナ"] = status[2] as int
      value["変化"] = status[3..status.size()-1].collect{it as int}
      valueList = [value["球速"], value["制球"], value["スタミナ"], value["変化"]].flatten()
    }
  }

  def isTarget() {
    // 限界突破
    def isLimitBreak = value.any {
      if (it.key == "変化") {
        return it.value.any { it > 100 || it < 0 }
      } else if (it.key == "球速") {
        return it.value > 160
      }
      return it.value > 100
    }
    if (isLimitBreak) {
      return true
    }

    if (type == "打者") {
      return [value["ミート"],value["パワー"],value["走力"],value["肩力"]].every{it >= 80}
    } else {
      if (value["球速"] >= 150 && value["制球"] >= 80) {
        if (value["スタミナ"] >= 80) {
          // 先発
          return value["変化"].every{it >= 70 }
        } else {
          // 中継ぎ
          return value["変化"].every{it >= 80 }
        }
      }
      return false
    }
  }
}
