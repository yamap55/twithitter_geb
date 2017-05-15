package twithitter

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*

class Outputer {
  def outputF
  Outputer(googleWebAplicationId, batterResultFile, pitcherResultFile) {
    if (googleWebAplicationId) {
      outputF = {user ->
        def http = new HTTPBuilder( "https://script.google.com/macros/s/${googleWebAplicationId}/" )
        http.post( path: 'exec', body: user.asJson(),
                  contentType: JSON ) { resp ->
          println "POST Success: ${resp.statusLine}"
        }
      }
    } else {
      outputF = { user ->
        def result = user.isBatter() ? batterResultFile : pitcherResultFile
        result << "${user.asCsvString()}\n"
      }
    }
  }

  void output(user) {
    outputF(user)
  }
}
