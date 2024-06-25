package com.example.projet42_androidapp

import android.os.AsyncTask
import android.util.Log
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.source.RemoteJWKSet
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jose.util.DefaultResourceRetriever
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import java.net.URL

class VerifyTokenTask(
    private val accessToken: String,
    private val callback: (Boolean, String?) -> Unit // Updated to return the token if valid
) : AsyncTask<Void, Void, Boolean>() {

    private var validatedToken: String? = null

    override fun doInBackground(vararg params: Void?): Boolean {
        return try {
            // URL of the JWKS endpoint
            val jwkSetURL = URL("http://192.168.1.29:8090/realms/projet42-realm/protocol/openid-connect/certs")
            Log.d("AuthToken", "JWKS URL: $jwkSetURL")

            // Setup resource retriever
            val resourceRetriever = DefaultResourceRetriever(2000, 2000, 1024 * 1024)
            Log.d("AuthToken", "ResourceRetriever created")

            // Setup RemoteJWKSet
            val remoteJWKSet = RemoteJWKSet<SecurityContext>(jwkSetURL, resourceRetriever)
            Log.d("AuthToken", "RemoteJWKSet created")

            // Retrieve and log public keys
            val jwkSet = JWKSet.load(jwkSetURL)
            val keys = jwkSet.keys
            for (key in keys) {
                Log.d("AuthToken", "Public Key: ${key.toJSONString()}")
            }

            // Create JWT processor
            val jwtProcessor = DefaultJWTProcessor<SecurityContext>()
            Log.d("AuthToken", "JWTProcessor created")

            // Setup JWS key selector
            val keySelector = JWSVerificationKeySelector(JWSAlgorithm.RS256, remoteJWKSet)
            Log.d("AuthToken", "KeySelector created")

            jwtProcessor.jwsKeySelector = keySelector

            // Process token verification
            val claimsSet = jwtProcessor.process(accessToken, null as SecurityContext?)
            Log.d("AuthToken", "Token claims: ${claimsSet.toJSONObject()}")

            // Validate token claims
            if (validateClaims(claimsSet)) {
                validatedToken = accessToken // Store the token if valid
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("AuthToken", "Token verification failed", e)
            false
        }
    }

    override fun onPostExecute(result: Boolean) {
        callback(result, validatedToken)
    }

    private fun validateClaims(claimsSet: JWTClaimsSet): Boolean {
        // Validate token claims, such as issuer and audience
        val issuer = claimsSet.issuer
        val audience = claimsSet.audience

        Log.d("AuthToken", "Issuer: $issuer")
        Log.d("AuthToken", "Audience: $audience")

        // Validate issuer and audience
        return issuer == "http://192.168.1.29:8090/realms/projet42-realm" && audience.contains("projet42-api")
    }
}
