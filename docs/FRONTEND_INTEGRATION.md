# Frontend + cloud integration — 2026-11-28

## Git state

Working branch: integration/frontend-cloud.
Input: origin/feature/frontend at dc70bc5 ("My designs").
Cloud backend base: 84ca271.
Local checkpoint f3c1c66 preserves automatic HTTPS sign-in and session-expiry cleanup.
The frontend merge is intentionally left for the user to commit after review.
Nothing has been pushed or merged into main by the agent.

The previously untracked .idea/vcs.xml was preserved in a named Git stash before
merging because the frontend branch tracks that path. Keep that stash until reviewed.

## Resolved overlaps and integration fixes

- ServerSignInScreen import conflict resolved; it now hosts the designed LoginScreen
  and SignUpScreen with the existing ViewModel callbacks.
- ServerConfig.BASE_URL remains automatic and points to the verified Render host.
  No URL entry is required and no database credential is included in Android.
- Welcome Log In and Staff/Admin Login both authenticate; account roles still come
  from the server. The outlined welcome action clearly opens the local demo.
- Signup collects username/password/confirmation. Full name/student ID fields were
  omitted from the active registration form because the current backend has no
  fields to persist them. Pickup name remains part of checkout.
- Designed home uses model.foods and actual returned prices/IDs, rather than a
  separate fixed four-food catalog or fabricated wallet balance.
- Menu shows food even without a photo and includes Snacks again.
- Local display images are mapped by known food IDs; they never change API data.
- Embedded student navigation callbacks are wired; the duplicate parent navigation
  bar was removed.
- Checkout offers only supported cash-on-pickup. No fake online payment is submitted.
- Online admin inventory uses CloudInventoryScreen and the real stock callback.
  The collaborator's local menu CRUD editor is retained for demo use only.
- Existing staff order transitions, JSON/CSV/XML reports, checkout retries and
  expired-session cleanup are preserved.
- Existing collaborator logo, food images, visual styling and screen files retained.
- The collaborator's icons dependency is included; no dependency version upgrade.

Main affected directory:
app/src/main/java/com/example/canteengo/

Integration files:
- ui/CanteenGoApp.kt
- ui/screens/ServerSignInScreen.kt
- ui/screens/LoginScreen.kt
- ui/screens/SignUpScreen.kt
- ui/screens/WelcomeScreen.kt
- ui/screens/HomeScreen.kt
- ui/screens/MenuScreen.kt
- ui/screens/CheckoutScreen.kt
- ui/screens/AdminScreen.kt
- ui/screens/CloudInventoryScreen.kt (new)
- data/DemoMenu.kt

## Verification

Build/unit test command:
    .\gradlew.bat :app:assembleDebug :app:testDebugUnitTest --console=plain

Build result: BUILD SUCCESSFUL in 4m 22s; 44 tasks executed, exit 0.
Both assembleDebug and testDebugUnitTest completed. Gradle sync also succeeded.
Test report files were excluded by IDE access policy; no test count is asserted.
Warnings: ReceiptLong icon deprecations and one native library packaged unstripped.
No backend/database code was changed by this frontend integration.
Machine-specific incoming emulator path was excluded; original JDK name retained.

Preview attempts before integration: Login, Signup and Welcome rendered.
Home, Checkout and Admin initially could not resolve the newly added icons dependency.
After successful sync, Home, Login, Signup, ServerSignIn wrapper and Checkout
previews rendered successfully. The wrapper initially used a stale Login method
signature in the preview cache; rendering the updated Login then the wrapper fixed
that preview error without changing compiled app code.
The original local IDE vcs directory mapping was restored; its stash is retained.

Manual connected workflows remain to be checked; do not treat a build or static
preview as proof of authentication, payment, inventory or order correctness.

## Phone acceptance checklist

- [ ] Install the newly built APK as an update.
- [ ] Welcome Log In opens the designed login screen, without a URL field.
- [ ] Signup creates a server student; invalid usernames/passwords and mismatched
      confirmation show validation feedback.
- [ ] Student sees server menu/prices, including foods without photos.
- [ ] Home categories and Menu/Cart/Orders navigation all work.
- [ ] Adding food, changing quantity and placing a cash order yields a CG- order.
- [ ] Sign out/in restores the saved server order.
- [ ] Staff can advance the order through preparation, ready and collected.
- [ ] Admin can update real stock and view masked reports.
- [ ] Session invalidation clears account state and returns to Welcome.
- [ ] Explore local demo remains clearly identified as local.

## Known scope

Online wallet/payments, profile editing, password-reset service, staff account CRUD
and online menu create/edit/delete APIs are not implemented by this merge.
Available-food endpoint still hides zero-stock entries; admins can restock by ID.
Render Free cold starts can exceed app timeouts; wake /api/health before a demo.
R8/R11/R12/R17 improved, not certified complete.

## User commands after review

    git add app/ docs/ gradle/
    git commit -m "Merge frontend designs with cloud authentication and ordering"
    git push -u origin integration/frontend-cloud

Then open a PR from integration/frontend-cloud to main and have a teammate review.
The merge includes staged files from the collaborator; inspect git status before
committing. No automatic push, PR merge, schema migration or backend change here.
