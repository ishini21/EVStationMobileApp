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

class SplashActivity : AppCompatActivity() {

    private lateinit var logoCard: CardView
    private lateinit var appNameText: TextView
    private lateinit var tagline: TextView
    private lateinit var circleBackground1: View
    private lateinit var circleBackground2: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

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

        // Navigate to main activity after delay
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToMainActivity()
        }, 3000) // 3 seconds
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

        val scaleXCircle2 = ObjectAnimator.ofFloat(circleBackground2, "scaleX", 1f, 1.3f, 1f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }

        val scaleYCircle2 = ObjectAnimator.ofFloat(circleBackground2, "scaleY", 1f, 1.3f, 1f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }

        rotateCircle1.start()
        scaleXCircle1.start()
        scaleYCircle1.start()
        rotateCircle2.start()
        scaleXCircle2.start()
        scaleYCircle2.start()
    }

    private fun animateLogo() {
        // Initial state
        logoCard.scaleX = 0f
        logoCard.scaleY = 0f
        logoCard.alpha = 0f

        // Scale animation
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

        // Subtle pulse animation
        val pulseScaleX = ObjectAnimator.ofFloat(logoCard, "scaleX", 1f, 1.05f, 1f).apply {
            duration = 1500
            repeatCount = ObjectAnimator.INFINITE
            startDelay = 1000
            interpolator = AccelerateDecelerateInterpolator()
        }

        val pulseScaleY = ObjectAnimator.ofFloat(logoCard, "scaleY", 1f, 1.05f, 1f).apply {
            duration = 1500
            repeatCount = ObjectAnimator.INFINITE
            startDelay = 1000
            interpolator = AccelerateDecelerateInterpolator()
        }

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, fadeIn)
            start()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    pulseScaleX.start()
                    pulseScaleY.start()
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
    }

    private fun animateText() {
        // App name animation
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

        // Tagline animation with slight delay
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

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        // Fade transition
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

}