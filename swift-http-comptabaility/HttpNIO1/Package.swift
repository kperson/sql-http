// swift-tools-version:5.0.0
import PackageDescription

let package = Package(
    name: "swift-http-nio1",
    products: [
        .library(name: "HttpNIO1", targets: ["HttpNIO1"]),
        
    ],
    dependencies: [
        .package(path: "../base"),
        .package(url: "https://github.com/kperson/swift-basic-http.git", .branch("master"))

    ],
    targets: [
        .target(
            name: "HttpNIO1",
            dependencies: [
                "HttpExecuter",
                "SwiftBasicHTTP"
            ]
        )
    ]
)
