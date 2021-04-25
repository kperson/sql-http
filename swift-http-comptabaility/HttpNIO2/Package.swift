// swift-tools-version:5.0.0
import PackageDescription

let package = Package(
    name: "swift-http-nio2",
    products: [
        .library(name: "HttpNIO2", targets: ["HttpNIO2"]),
        
    ],
    dependencies: [
        .package(path: "../base"),
        .package(url: "https://github.com/swift-server/async-http-client.git", from: "1.0.0")
    ],
    targets: [
        .target(
            name: "HttpNIO2",
            dependencies: [
                "HttpExecuter",
                "AsyncHTTPClient"
            ]
        )
    ]
)
