def p = "/Users/yamap_55/Desktop/twithitter_batter.txt"
def f = new File(p)

// def header = ["名前", "チーム", "種別", "球速", "制球", "スタミナ", "変化1", "変化2", "変化3", "変化4"]
def header = ["名前", "チーム", "種別", "ミート", "パワー", "走力", "肩力", "捕", "一", "二", "三", "遊", "左", "中", "右"]

println header.join(",")

f.eachLine {
  if (it =~ /Good?/) {
    return
  }
  def l = it.split(" : ")
  def name = l[0]
  def type = l[1]
  def status = Eval.me(l[2] - "Status(" - ")")
  def values = status.values()
  // def speed = status["球速"]
  // def control = status["制球"]
  // def stamina = status["スタミナ"]
  // def breakingBall = status["変化"]

  // println ([name, "", type, speed, control, stamina, breakingBall].flatten().join(","))
  println ([name, "", type, values].flatten().join(","))

//  println "${name},${type},${speed},${control},${}"
}
