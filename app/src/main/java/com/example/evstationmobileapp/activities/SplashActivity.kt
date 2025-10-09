package com.example.evstationmobileapp.activities

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.evstationmobileapp.MainActivity
import com.example.evstationmobileapp.R
import com.example.evstationmobileapp.utils.SessionManager

class SplashActivity : AppCompatActivity() {

    private lateinit var logoCard: CardView
    private lateinit var appNameText: TextView
    private lateinit var tagline: TextView
    private lateinit var circleBackground1: View
    private lateinit var circleBackground2: View
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensure your splash screen layout is named 'splash_screen.xml' or update this line
        setContentView(R.layout.splash_screen)

        sessionManager = SessionManager(this)

        // Hide status bar for immersive experience
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)

        // Initialize views
        logoCard = findViewById(R.id.logoCard)
        appNameText = findViewById(R.id.appNameText)
        tagline = findViewById(R.id.tagline)
        circleBackground1 = findViewById(R.id.circleBackground1)
        circleBackground2 = findViewById(R.id.circleBackground2)

        // Start animations
        startAnimations()

        // Navigate to the next screen after a delay
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 3000) // 3-second delay
    }

    private fun startAnimations() {
        // Animate background circles
        animateBackgroundCircles()

        // Animate logo with scale and fade
        animateLogo()

        // Animate text with delay
        Handler(Looper.getMainLooper()).postDelayed({
            animateText()
        }, 800)
    }

    private fun animateBackgroundCircles() {
        // Circle 1 - Rotate and scale
        val rotateCircle1 = ObjectAnimator.ofFloat(circleBackground1, "rotation", 0f, 360f).apply {
            duration = 20000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        val scaleXCircle1 = ObjectAnimator.ofFloat(circleBackground1, "scaleX", 1f, 1.2f, 1f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        val scaleYCircle1 = ObjectAnimator.ofFloat(circleBackground1, "scaleY", 1f, 1.2f, 1f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Circle 2 - Rotate opposite direction
        val rotateCircle2 = ObjectAnimator.ofFloat(circleBackground2, "rotation", 0f, -360f).apply {
            duration = 25000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Start all background animations
        AnimatorSet().apply {
            playTogether(rotateCircle1, scaleXCircle1, scaleYCircle1, rotateCircle2)
            start()
        }
    }

    private fun animateLogo() {
        logoCard.scaleX = 0f
        logoCard.scaleY = 0f
        logoCard.alpha = 0f

        val scaleX = ObjectAnimator.ofFloat(logoCard, "scaleX", 0f, 1f).apply {
            duration = 800
            interpolator = OvershootInterpolator()
        }
        val scaleY = ObjectAnimator.ofFloat(logoCard, "scaleY", 0f, 1f).apply {
            duration = 800
            interpolator = OvershootInterpolator()
        }
        val fadeIn = ObjectAnimator.ofFloat(logoCard, "alpha", 0f, 1f).apply {
            duration = 600
        }

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, fadeIn)
            start()
        }
    }

    private fun animateText() {
        val fadeInName = ObjectAnimator.ofFloat(appNameText, "alpha", 0f, 1f).apply {
            duration = 600
        }
        val slideUpName = ObjectAnimator.ofFloat(appNameText, "translationY", 50f, 0f).apply {
            duration = 600
            interpolator = AccelerateDecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(fadeInName, slideUpName)
            start()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val fadeInTagline = ObjectAnimator.ofFloat(tagline, "alpha", 0f, 1f).apply {
                duration = 600
            }
            val slideUpTagline = ObjectAnimator.ofFloat(tagline, "translationY", 30f, 0f).apply {
                duration = 600
                interpolator = AccelerateDecelerateInterpolator()
            }
            AnimatorSet().apply {
                playTogether(fadeInTagline, slideUpTagline)
                start()
            }
        }, 200)
    }

    private fun navigateToNextScreen() {
        // Use the SessionManager to check the user's login status
        val intent = if (sessionManager.isLoggedIn()) {
            // User is logged in, go to MainActivity (Dashboard)
            Intent(this, MainActivity::class.java)
        } else {
            // User is not logged in, go to LoginActivity
            Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        // Add a fade transition for a smooth navigation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish() // Finish SplashActivity so the user can't go back to it
    }
}