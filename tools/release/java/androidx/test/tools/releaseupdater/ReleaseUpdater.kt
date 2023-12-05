package androidx.test.tools.releaseupdater

/**
 *
 */
class ReleaseUpdater {

  /** Validates that the version is correctly incremented.
   *
   * "Correct" increments fall under two categories:
   *
   * 1) X.X.X (stable) -> Y.Y.Y-alpha01 where
   *   validateVersionNumberIncrement('X.X.X', 'Y.Y.Y') passes
   * 2) X.X.X-suffix1 -> X.X.X-suffix2 where
   *   validateSuffixIncrement(suffix1, suffix2) passes
   *
   * For more details (and the full source-of-truth), see go/androidx/versioning. // copybara:strip
   *
   * @param old_version The original version
   * @param new_version The incremented "+1" version
   *
   * @throws IllegalArgumentException If the version was incorrectly incremented
   */
  fun validateVersions(old_version: String, new_version: String): Unit {
    if (new_version.indexOf("-") == -1) {
      // if new_version doesn't have a -, it means it is a stable version, which
      // means 'old_version' must be an rc with the same version number
      if (old_version.indexOf("-") == -1) {
        // we don't currently support stable -> stable
        throw invalidVersionNumberException(new_version, old_version)
      }
      val old_version_num = old_version.split("-")[0]
      val old_version_suffix = old_version.split("-")[1]
      if (new_version != old_version_num || !old_version_suffix.startsWith("rc")) {
        throw invalidVersionException(new_version, old_version)
      }
    } else if (old_version.indexOf("-") == -1) {
      // if old_version doesn't have a -, it means it is a stable version, which
      // means 'new_version' must be an alpha01 of the next version number
      val new_version_num = new_version.split("-")[0]
      val new_version_suffix = new_version.split("-")[1]
      if (new_version_suffix != "alpha01") {
        throw invalidVersionException(new_version, old_version)
      }

      validateVersionNumberIncrement(old_version, new_version_num)
    } else {
      val old_version_num = old_version.split("-")[0]
      val old_version_suffix = old_version.split("-")[1]
      val new_version_num = new_version.split("-")[0]
      val new_version_suffix = new_version.split("-")[1]

      if (old_version_num == new_version_num) {
        // same number, so check the alpha/beta/rc part
        validateSuffixIncrement(old_version_suffix, new_version_suffix)
      } else {
        // we've already explicitly checked for the two cases where the numbers are different, so
        // we know this one is invalid
        throw invalidVersionException(new_version, old_version)
      }
    }
  }

  /** Validates that the suffix is correctly incremented.
   *
   * "Correct" increments fall under two categories:
   *
   * 1) alphaXX -> beta01, betaXX -> rc01
   * 2) alphaXX -> alphaYY, betaXX -> betaYY, rcXX -> rcYY, where XX + 1 = YY
   *
   * Used in alpha/beta/rc releases. Stable releases do not have a suffix and thus
   * this method does not handle them.
   *
   * @param old_suffix The original suffix in (alpha/beta/rc)XX format
   * @param new_suffix The "incremented" suffix in (alpha/beta/rc)XX format
   *
   * @throws IllegalArgumentException If the suffix was incorrectly incremented
   *
   */
  private fun validateSuffixIncrement(old_suffix: String, new_suffix: String): Unit {
    val ordering = listOf("alpha", "beta", "rc")
    val old_alpha_beta_or_rc = old_suffix.dropLast(2)
    val old_suffix_num = old_suffix.takeLast(2)
    val new_alpha_beta_or_rc = new_suffix.dropLast(2)
    val new_suffix_num = new_suffix.takeLast(2)

    if (!ordering.contains(old_alpha_beta_or_rc) || !ordering.contains(new_alpha_beta_or_rc)) {
      throw invalidSuffixException(new_suffix, old_suffix)
    }

    if (new_suffix_num == "01") {
      // ensure alpha -> beta01 or beta -> rc01
      if (ordering.indexOf(old_alpha_beta_or_rc) + 1 != ordering.indexOf(new_alpha_beta_or_rc)) {
        throw invalidSuffixException(new_suffix, old_suffix)
      }
    } else if (old_alpha_beta_or_rc != new_alpha_beta_or_rc) {
      // already checked alpha -> beta or beta -> rc, so if they're still different, it's some
      // invalid combination
      throw invalidSuffixException(new_suffix, old_suffix)
    } else {
      // both alpha or both beta or both rc, so just need to make sure that the number at the end
      // is getting incremented by 1
      if (old_suffix_num.toInt(10) + 1 != new_suffix_num.toInt(10)) {
        throw invalidSuffixException(new_suffix, old_suffix)
      }
    }
  }

  /** Validates that the version number is correctly incremented.
   *
   * @param old_number The original version number in major.minor.bugfix format
   * @param new_number The "incremented" version number in major.minor.bugfix format
   *
   * @throws IllegalArgumentException If the version number was incorrectly incremented
   */
  private fun validateVersionNumberIncrement(old_number: String, new_number: String): Unit {
    val old_major = old_number.split(".")[0].toInt()
    val old_minor = old_number.split(".")[1].toInt()
    val new_major = new_number.split(".")[0].toInt()
    val new_minor = new_number.split(".")[1].toInt()
    val new_bugfix = new_number.split(".")[2].toInt()

    if (old_major + 1 == new_major) {
      if (new_minor != 0 || new_bugfix != 0) {
        throw invalidVersionNumberException(new_number, old_number)
      }
    } else if (old_major != new_major) {
      throw invalidVersionNumberException(new_number, old_number)
    } else if (old_minor + 1 == new_minor) {
      if (new_bugfix != 0) {
        throw invalidVersionNumberException(new_number, old_number)
      }
    } else if (old_minor != new_minor) {
      throw invalidVersionNumberException(new_number, old_number)
    } else {
      // major and minor are the same. But we don't currently want to support "bugfix-only" updates
      // via this library, (or the version didn't get incremented at all) so we throw
      throw invalidVersionNumberException(new_number, old_number)
    }
  }

  companion object {
    private fun invalidSuffixException(new_suffix: String, old_suffix: String) =
      IllegalArgumentException(String.format("Invalid suffix %s after %s", new_suffix, old_suffix))
    private fun invalidVersionException(new_version: String, old_version: String) =
      IllegalArgumentException(String.format("Invalid version %s after %s", new_version, old_version))

    private fun invalidVersionNumberException(new_number: String, old_number: String) =
      IllegalArgumentException(String.format("Invalid version number %s after %s", new_number, old_number))

    private val artifactMap: Map<String, Array<String>> = mapOf(
      "Annotation" to arrayOf("//annotation/java/androidx/test/annotation:annotation_maven_artifact"),
      "Core" to arrayOf(
        "//core/java/androidx/test/core:core_maven_artifact",
        "//ktx/core/java/androidx/test/core:core_maven_artifact",
      ),
      "Espresso" to arrayOf(
        "//espresso/accessibility/java/androidx/test/espresso/accessibility:accessibility_checks_maven_artifact",
        "//espresso/contrib/java/androidx/test/espresso/contrib:espresso_contrib_maven_artifact",
        "//espresso/core/java/androidx/test/espresso:espresso_core_maven_artifact",
        "//espresso/idling_resource/concurrent/java/androidx/test/espresso/idling/concurrent:idling_concurrent_maven_artifact",
        "//espresso/idling_resource/java/androidx/test/espresso:espresso_idling_resource_maven_artifact",
        "//espresso/idling_resource/net/java/androidx/test/espresso/idling/net:idling_net_maven_artifact",
        "//espresso/intents/java/androidx/test/espresso/intent:espresso_intents_maven_artifact",
        "//espresso/remote/java/androidx/test/espresso/remote:espresso_remote_maven_artifact",
        "//espresso/web/java/androidx/test/espresso/web:espresso_web_maven_artifact",
      ),
      "Espresso Device" to arrayOf(
        "//espresso/device/java/androidx/test/espresso/device:device_maven_artifact",
      ),
      "JUnit Extensions" to arrayOf(
        "//ext/junit/java/androidx/test/ext/junit:junit_maven_artifact",
        "//ktx/ext/junit/java/androidx/test/ext/junit:junit_maven_artifact",
      ),
      "Truth Extensions" to arrayOf(
        "//ext/truth/java/androidx/test/ext/truth:truth_maven_artifact",
      ),
      "Monitor" to arrayOf(
        "//runner/monitor/java/androidx/test:monitor_maven_artifact",
      ),
      "Orchestrator" to arrayOf(
        "//runner/android_test_orchestrator/stubapp:orchestrator_release_maven_artifact",
      ),
      "Runner" to arrayOf(
        "//runner/android_junit_runner/java/androidx/test:runner_maven_artifact",
      ),
      "Rules" to arrayOf(
        "//runner/rules/java/androidx/test:rules_maven_artifact",
      ),
      "Services" to arrayOf(
        "//services:test_services_maven_artifact",
        "//services/storage/java/androidx/test/services/storage:test_storage_maven_artifact",
      ),
    )
  }
}
