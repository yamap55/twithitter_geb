// def str = """@andiirma : 投手 : Status([球速:161, 制球:45, スタミナ:1, 変化:[97, 89, 53, 52]])
// @copephotos : 投手 : Status([球速:155, 制球:85, スタミナ:12, 変化:[99, 83]])
// @steveb2u : 投手 : Status([球速:161, 制球:94, スタミナ:63, 変化:[79, 5, 90]])"""
def str = """@YummyMimii : 打者 : Status([ミート:98, パワー:100, 走力:82, 肩力:89, 捕:87, 一:4, 二:75, 三:38, 遊:60, 左:61, 中:76, 右:32])
@stephaniesaylor : 打者 : Status([ミート:99, パワー:99, 走力:84, 肩力:98, 捕:34, 一:1, 二:45, 三:35, 遊:53, 左:33, 中:84, 右:76])
@Alyssa700 : 打者 : Status([ミート:84, パワー:80, 走力:92, 肩力:83, 捕:3, 一:28, 二:32, 三:50, 遊:31, 左:15, 中:25, 右:36])"""

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
