// swift-tools-version:5.0.0
import PackageDescription

let package = Package(
    name: "rds-data-example-app",
    products: [
        .library(name: "RDSDataExampleApp", targets: ["RDSDataExampleApp"])
    ],
    dependencies: [
        .package(url: "../", .branch("master"))
    ],
    targets: [
        .target(
            name: "RDSDataExampleApp",
            dependencies: [
                "RDSData"
            ]
        )
    ]
)
