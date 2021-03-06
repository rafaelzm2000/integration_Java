package com.veracode.hmac_request_signing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;

public class Main {

// Introducing a flaw
String password = "password";
String password1 = "Password";
String password2 = "Password";
String password3 = "Password";

private static final String URL_BASE = "api.veracode.com";
private static final String URL_PATH = "/appsec/v1/applications/";
private static final String GET = "GET";
private static final String APP_GUID = "597129";
private static final String ACCESS_KEY_ID = "dbb6f2a2ed0b6890bbd32e949f72c8c8";
private static final String SECRET_ACCESS_KEY = "d7a7f43393deb511091c9eee03a2a573cedd740a1d7a5a8d09aae22c83c3c4d75808a57eb4c1e4100476db8feafb14ef25752be2cc912fed8e13878d4548ab76";

/**
* The main method for our demo.  This makes a simple API call using our example HMAC signing class
* and writes the response to the output stream.
*
* @param args command line arguments - ignored
*/
public static void main(final String[] args) {
try {
/*
* Combine the URL base with the specific URL endpoint we wish to access.
* This is REST, so the GUID we are accessing is in the URL.
*/
final URL applicationsApiUrl = new URL("https://" + URL_BASE + URL_PATH + APP_GUID);

/*
* Now we use the url above and our example HMAC signer class to generate a Veracode HMAC header for later use.
*/
final String authorizationHeader = HmacRequestSigner.getVeracodeAuthorizationHeader(ACCESS_KEY_ID, SECRET_ACCESS_KEY, applicationsApiUrl, GET);

/*
* Here we are using Java built in HTTPS protocols to handle making a call to the API's URL.
* We also set the request method to GET.
*/
final HttpsURLConnection connection = (HttpsURLConnection) applicationsApiUrl.openConnection();
connection.setRequestMethod(GET);

/*
* This is where we add the Authorization header with the value returned by our example HMAC signer class.
*/
connection.setRequestProperty("Authorization", authorizationHeader);

/*
* Now we just need to make the actual call by opening up the response stream and read from it.
*/
try (InputStream responseInputStream = connection.getInputStream()) {
try {
	readResponse(responseInputStream);
} catch (JSONException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
}
} catch (InvalidKeyException | NoSuchAlgorithmException | IllegalStateException | IOException e) {
e.printStackTrace();
}
}

/*
* A simple method to read an input stream (containing JSON) to System.out.
*/
private static void readResponse(InputStream responseInputStream) throws IOException, JSONException {
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
byte[] responseBytes = new byte[16384];
int x = 0;
while ((x = responseInputStream.read(responseBytes, 0, responseBytes.length)) != -1) {
outputStream.write(responseBytes, 0, x);
}
outputStream.flush();
System.out.println((new JSONObject(outputStream.toString())).toString(4));
}

}