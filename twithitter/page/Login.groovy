package twithitter.page
import geb.Page

class Login extends Page {
  static url = "https://twithitter.com/login"
  static at = { waitFor { title == "Twitter / アプリケーション認証" } }
  static content = {
    userNameField {$("#username_or_email")}
    passwordField { $("#password") }
    loginButton { $(".submit").first() }
  }

  def login(id, password) {
    userNameField.value(id)
    passwordField.value(password)
    loginButton.click()
    waitFor{ title == "TwitHitter" }
  }
}
