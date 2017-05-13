package twithitter

import groovy.transform.ToString

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
