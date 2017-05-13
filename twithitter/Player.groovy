package twithitter

import groovy.json.JsonBuilder

class Player {
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

  Player(profileArea, playerType, statusArea) {
    def f = {
      profileArea.$(".${it}").first().text()
    }
    name = f("name")
    twitterId = f("screen-name")
    throwBat = f("throw-bat")
    team = f("userteam-none-label")
    popularity = profileArea.find(".userteam-popularity").first().text().split(" ")[1]

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
