// swift-tools-version:5.0.0
import PackageDescription

let package = Package(
    name: "sql-http",
    products: [
        .library(name: "SQLHttp", targets: ["SQLHttp"])
    ],
    dependencies: [
        .package(path: "../swift-http-comptabaility/base")
    ],
    targets: [
        .target(
            name: "SQLHttp",
            dependencies: ["HttpExecuter"]
        ),
        .testTarget(
            name: "SQLHttpTests",
            dependencies: [
                "SQLHttp"
            ]
        )
    ]
)
