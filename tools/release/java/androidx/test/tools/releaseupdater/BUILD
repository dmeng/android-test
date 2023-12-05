load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_binary", "kt_jvm_library")

package(
    default_visibility = [
        "//:__subpackages__",
    ],
)

kt_jvm_library(
    name = "release_updater_lib",
    srcs = glob([
        "ReleaseUpdater.kt",
    ]),
)

kt_jvm_binary(
    name = "releaseupdater",
    srcs = ["Main.kt"],
    main_class = "androidx.test.tools.releaseupdater.Main",
    deps = [
        ":release_updater_lib",
    ],
)
