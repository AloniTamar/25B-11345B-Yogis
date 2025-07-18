package com.tamara.a25b_11345b_yogis.utils
import android.view.View
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.data.firebase.AuthManager
import com.tamara.a25b_11345b_yogis.ui.main.MainGuestFragment
import com.tamara.a25b_11345b_yogis.ui.main.MainLoggedInFragment
import com.tamara.a25b_11345b_yogis.R

fun Fragment.navigateSmoothly(to: Fragment, addToBackStack: Boolean = true) {
    parentFragmentManager.beginTransaction()
        .setCustomAnimations(
            R.anim.fade_in,   // enter
            R.anim.fade_out,  // exit
            R.anim.fade_in,   // popEnter
            R.anim.fade_out   // popExit
        )
        .replace(R.id.container, to)
        .apply { if (addToBackStack) addToBackStack(null) }
        .commit()
}

fun Fragment.wireBack(btn: View) {
    btn.setOnClickListener { parentFragmentManager.popBackStack() }
}

fun Fragment.navigateBackToMain() {
    val destination = if (AuthManager.currentUserUid() != null) {
        MainLoggedInFragment()
    } else {
        MainGuestFragment()
    }
    navigateSmoothly(destination, addToBackStack = false)
}