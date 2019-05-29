package dev.olog.msc.app.injection.navigator

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import dev.olog.msc.app.injection.R
import dev.olog.msc.presentation.about.licenses.LicensesFragment
import dev.olog.msc.presentation.about.thanks.SpecialThanksFragment
import dev.olog.msc.presentation.base.extensions.fragmentTransaction
import dev.olog.msc.presentation.base.extensions.isIntentSafe
import dev.olog.msc.presentation.base.openPlayStore
import dev.olog.msc.presentation.navigator.NavigatorAbout
import dev.olog.msc.shared.extensions.toast
import javax.inject.Inject

private const val NEXT_REQUEST_THRESHOLD: Long = 400 // ms

class NavigatorAboutImpl @Inject constructor() : NavigatorAbout {

    private var lastRequest: Long = -1

    override fun toLicensesFragment(activity: FragmentActivity) {
        if (allowed()) {
            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(R.id.fragment_container, LicensesFragment(), LicensesFragment.TAG)
                addToBackStack(LicensesFragment.TAG)
            }
        }
    }

    override fun toSpecialThanksFragment(activity: FragmentActivity) {
        if (allowed()) {
            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(R.id.fragment_container, SpecialThanksFragment(), SpecialThanksFragment.TAG)
                addToBackStack(SpecialThanksFragment.TAG)
            }
        }
    }

    override fun toMarket(activity: FragmentActivity) {
        if (allowed()) {
            openPlayStore(activity)
        }
    }

    override fun toPrivacyPolicy(activity: FragmentActivity) {
        if (allowed()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://deveugeniuolog.wixsite.com/next/privacy-policy")
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast("Browser not found")
            }
        }
    }

    override fun joinCommunity(activity: FragmentActivity) {
        if (allowed()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://plus.google.com/u/1/communities/112263979767803607353")
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast("Browser not found")
            }
        }
    }

    override fun joinBeta(activity: FragmentActivity) {
        if (allowed()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/apps/testing/dev.olog.msc")
            if (activity.packageManager.isIntentSafe(intent)) {
                activity.startActivity(intent)
            } else {
                activity.toast("Browser not found")
            }
        }
    }

    private fun allowed(): Boolean {
        val allowed = (System.currentTimeMillis() - lastRequest) > NEXT_REQUEST_THRESHOLD
        lastRequest = System.currentTimeMillis()
        return allowed
    }

}