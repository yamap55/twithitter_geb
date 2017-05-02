@Grapes([
    @Grab("org.gebish:geb-core:1.1.1"),
    @Grab("org.seleniumhq.selenium:selenium-support:3.4.0"),
])

import geb.Browser
import groovy.transform.ToString


// def batterResultPath = "/Users/yamap_55/Desktop/twithitter_batter.txt"
// def pitcherResultPath = "/Users/yamap_55/Desktop/twithitter_pitcher.txt"
def batterResultPath = "./result/batter.csv"
def pitcherResultPath = "./result/pitcher.csv"

println "start ${new Date().format('yyyy/MM/dd HH:mm:ss')}"

def batterResultFile = new File(batterResultPath)
def pitcherResultFile = new File(pitcherResultPath)

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
    go "https://twithitter.com/login"
    $("#username_or_email").value(browser.config.rawConfig.loginTwitterId)
    $("#password").value(browser.config.rawConfig.loginTwitterPassword)
    $(".submit")[0].click()
    waitFor{ title == "TwitHitter" }

    while(num > playerCount) {
      if (prinlntCount <= playerCount) {
        prinlntCount += 100
        println "${new Date().format('yyyy/MM/dd HH:mm:ss')}, ${playerCount}/${num}, error : ${errorCount}"
      }

      // スカウトのランダムページからIDを取得
      go "https://twithitter.com/scout"
      waitFor { $(".search-random") && $(".search-result") }
      sleep(500)// TODO
      $(".search-random").click()
      waitFor { $(".player") }
      $(".player .screen-name")*.text().collect{it - "@"}.each {
        playerCount++
        // 取得したIDを元に、Playerのデータを取得
        go "https://twithitter.com/${it}"
        def batterStatusArea = $(".batter-status")
        def pitcherStatusArea = $(".pitcher-status")
        if (!batterStatusArea && !pitcherStatusArea) {
          // 存在しないIDの場合
          errorCount++
          println "errorid : ${it}"
          return
        }
        def type = batterStatusArea ? "打者" : "投手"
        def user = new User($(".player-profile")[0], batterStatusArea, pitcherStatusArea)

        if (user.status.isTarget()) {
          def r = "${user.twitterId} : ${user.type} : ${user.status}"
          println r
          def result = user.isBatter() ? batterResultFile : pitcherResultFile
          result << "${user}\n"
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

//@ToString(excludes = ["profileArea", "rank"])
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

  User(profileArea, batterStatusArea, pitcherStatusArea) {
    def f = {
      profileArea.$(".${it}")[0].text()
    }
    name = f("name")
    twitterId = f("screen-name")
    throwBat = f("throw-bat")
    team = f("userteam-none-label")
    popularity = profileArea.find(".userteam-popularity")[0].text().split(" ")[1]

    type = batterStatusArea ? "打者" : "投手"
    if (batterStatusArea) {
      // 打者の場合
      def area = batterStatusArea[0]
      def s = area.find(".left-column")[0].find(".status-val")*.text()
      status = new Status(type, s)
    } else {
      // 投手の場合
      def area = pitcherStatusArea[0]
      def s = area.find(".status-val")*.text()
      status = new Status(type, s)
    }
  }
  @Override
  public String toString() {
    [twitterId,"",type,status.valueList].flatten().join(",")
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
