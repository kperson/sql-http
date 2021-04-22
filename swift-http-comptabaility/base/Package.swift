// swift-tools-version:5.0.0
import PackageDescription

let package = Package(
    name: "swift-http-comptabaility-base",
    products: [
        .library(name: "HttpExecuter", targets: ["HttpExecuter"])
    ],
    targets: [
        .target(
            name: "HttpExecuter"
        )
    ]
)
