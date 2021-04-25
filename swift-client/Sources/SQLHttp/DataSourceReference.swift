import Foundation

public struct UsernamePassword: Codable, Equatable {
    
    let username: String
    let password: Masked
    
    public init(username: String, password: Masked) {
        self.username = username
        self.password = password
    }
    
}

public struct DataSource: Codable, Equatable {
    
    let jdbcURL: String
    let credentials: UsernamePassword?
    let properties: [String: String]?
    
    public init(jdbcURL: String, credentials: UsernamePassword? = nil, properties: [String: String]? = nil) {
        self.jdbcURL = jdbcURL
        self.credentials = credentials
        self.properties = properties
    }

}

public enum DataSourceReference: Codable, Equatable {
    
    case direct(DataSource)
    case custom(String)
    
    public init(from decoder: Decoder) throws {
        let dataType = try decoder.singleValueContainer().decode(EnumerationWrapperDataType.self).dataType
        switch dataType {
        case "Direct":
            let value = try decoder.singleValueContainer().decode(EnumerationWrapper<DataSource>.self).data!
            self = .direct(value)
        case "Custom":
            let value = try decoder.singleValueContainer().decode(EnumerationWrapper<String>.self).data!
            self = .custom(value)
        default:
            throw UnableToDecodeError(dataType: dataType, targetType: "DataSourceReference")
        }
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        switch self {
        case .direct(let value): try container.encode(EnumerationWrapper(dataType: "Direct", data: value))
        case .custom(let value): try container.encode(EnumerationWrapper(dataType: "Custom", data: value))
        }
    }
    
    public static func == (lhs: DataSourceReference, rhs: DataSourceReference) -> Bool {
        switch (lhs, rhs) {
        case (.direct(let one), .direct(let two)): return one == two
        case (.custom(let one), .custom(let two)): return one == two
        default: return false
        }
    }
    
    
    
}
