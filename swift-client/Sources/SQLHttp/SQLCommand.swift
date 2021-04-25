import Foundation

public struct Query: Codable, Equatable {
    
    public let dataSourceReference: DataSourceReference
    public let sql: String
    public let params: [String : SQLValue]
    
    public init(dataSourceReference: DataSourceReference, sql: String, params: [String : SQLValue] = [:]) {
        self.dataSourceReference = dataSourceReference
        self.sql = sql
        self.params = params
    }
    
}

public struct Write: Codable, Equatable {
    
    public let dataSourceReference: DataSourceReference
    public let sql: String
    public let params: [String : SQLValue]
    
    public init(dataSourceReference: DataSourceReference, sql: String, params: [String : SQLValue] = [:]) {
        self.dataSourceReference = dataSourceReference
        self.sql = sql
        self.params = params
    }
    
}

public struct BatchWrite: Codable, Equatable {
    
    public let dataSourceReference: DataSourceReference
    public let sql: String
    public let params: [[String : SQLValue]]
    
    public init(dataSourceReference: DataSourceReference, sql: String, params: [[String : SQLValue]] = []) {
        self.dataSourceReference = dataSourceReference
        self.sql = sql
        self.params = params
    }
    
}

public enum SQLCommand: Codable, Equatable {
        
    
    case query(Query)
    case write(Write)
    case batchWrite(BatchWrite)
    
    public init(from decoder: Decoder) throws {
        let dataType = try decoder.singleValueContainer().decode(EnumerationWrapperDataType.self).dataType
        switch dataType {
        case "Query":
            let value = try decoder.singleValueContainer().decode(EnumerationWrapper<Query>.self).data!
            self = .query(value)
        case "Write":
            let value = try decoder.singleValueContainer().decode(EnumerationWrapper<Write>.self).data!
            self = .write(value)
        case "BatchWrite":
            let value = try decoder.singleValueContainer().decode(EnumerationWrapper<BatchWrite>.self).data!
            self = .batchWrite(value)
        default:
            throw UnableToDecodeError(dataType: dataType, targetType: "SQLCommand")
        }
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        switch self {
        case .query(let value): try container.encode(EnumerationWrapper(dataType: "Query", data: value))
        case .write(let value): try container.encode(EnumerationWrapper(dataType: "Write", data: value))
        case .batchWrite(let value): try container.encode(EnumerationWrapper(dataType: "BatchWrite", data: value))
        }
    }
    
    public static func == (lhs: SQLCommand, rhs: SQLCommand) -> Bool {
        switch (lhs, rhs) {
        case (.query(let one), .query(let two)): return one == two
        case (.write(let one), .write(let two)): return one == two
        case (.batchWrite(let one), .batchWrite(let two)): return one == two
        default: return false
        }
    }
    
    
}
