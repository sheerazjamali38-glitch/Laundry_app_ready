# Implementation Notes — Navigation & UX Overhaul

This pass rebuilds the app's navigation and screen flow to match the
requested pattern (splash -> persistent 5-tab bottom nav -> service/category
tabbed item selection -> dedicated cart tab), while reusing the existing
models, Retrofit client, item catalog, and pricing logic untouched.

## What changed

- **`screens/SplashScreen.kt` (new)** - logo + tagline fade in, tagline fades
  out at ~1s, auto-navigates to Home at ~2s. No buttons.
- **`screens/BottomNavBar.kt` (new)** - 5-destination `NavigationBar`
  (Home / Cart / WhatsApp / History / Profile), blue/cyan-tinted icons only,
  with a badge on the Cart icon showing the live item count.
- **`MainActivity.kt` (rewritten)** - wraps the app in a `Scaffold` with the
  bottom bar shown on all main tabs (hidden on Splash and the Invoice detail
  screen). A single `CartViewModel` instance is created once here (scoped to
  the Activity) and passed down explicitly to Item Selection and Cart so the
  cart survives switching tabs.
- **`viewmodels/CartViewModel.kt` (replaces `OrderViewModel.kt`)** - same
  rates-fetching/pricing/submit logic as before, restructured as the single
  shared source of truth for cart contents, now exposing a `lines: List<CartLine>`
  helper for the Cart screen's row list.
- **`screens/ItemSelectionScreen.kt` (replaces `OrderScreen.kt`)** - top row
  of service-type tabs (Wash + Iron / Iron / Urgent), second row of category
  tabs (Men / Ladies), and a filtered list below (items with no price for the
  selected service type are hidden, using the existing rates map). A small
  "View Cart (N items)" bar appears once items are added.
- **`screens/CartScreen.kt` (new)** - cart line list with per-line qty/remove,
  empty-cart state, the customer details form + totals + "Place Order" button
  that used to live on the Order screen, and the order-confirmation dialog.
- **`screens/HistoryScreen.kt` (rewritten)** - same `HistoryViewModel`/API
  wiring, now split into "Ongoing" and "History" sub-tabs using the existing
  `status` field (`Delivered`/`Completed` = History, everything else =
  Ongoing), with matching empty states.
- **`screens/ProfileScreen.kt` (new)** - no login. Shows/edits the locally
  stored name, mobile number, and address (now persisted via an expanded
  `Prefs`), plus static business info (contact number, WhatsApp link, service
  areas).
- **`screens/WhatsAppScreen.kt` (new)** - single "Chat With Us" button opening
  `wa.me/923478164692`, used as the WhatsApp tab's content.
- **`screens/HomeScreen.kt` (rewritten)** - dashboard layout: logo/tagline,
  one prominent "Place Order" card, a light "How it works" blurb, and the
  service-area note. No promotional banners, no store locator.
- **`data/Prefs.kt` (extended)** - now stores name and address alongside the
  mobile number, so Profile, Cart, and History all read/write the same local
  record.

## Unchanged (reused as-is)

- `api/ApiService.kt`, `api/RetrofitClient.kt`, `models/*`, `data/ItemCatalog.kt`,
  `data/Pricing.kt`, `data/Constants.kt`, `viewmodels/HistoryViewModel.kt`,
  `viewmodels/InvoiceViewModel.kt`, `screens/InvoiceScreen.kt`,
  `ui/theme/Color.kt`, `ui/theme/Theme.kt`.

## Things to double check before you ship

1. **Bank account details** - `data/Constants.kt` still has a placeholder
   `BANK_ACCOUNT_NUMBER`. Fill in the real details before release.
2. **Icons** - bottom nav and a few screens use `androidx.compose.material:material-icons-extended`
   (already a dependency), specifically `LocalLaundryService`, `Chat`,
   `Receipt`, `Inbox`, `RemoveShoppingCart`. If any of these ever get renamed
   upstream, swap for the nearest equivalent - none of the app's logic
   depends on the specific icon.
3. **History status values** - `HistoryScreen.kt`'s `isOngoing()` treats any
   status other than `"Delivered"`/`"Completed"` (case-insensitive) as
   Ongoing, per spec. If the backend uses different status strings, adjust
   `COMPLETED_STATUSES` in that file.
4. **Not compiled locally** - this sandbox has no Android SDK / no access to
   Google's Maven repo, so `gradle assembleDebug` couldn't be run here.
   Your GitHub Actions workflow (`.github/workflows/build.yml`) will run it on
   push - check that build's output and paste back any errors if it doesn't
   go green on the first try.
