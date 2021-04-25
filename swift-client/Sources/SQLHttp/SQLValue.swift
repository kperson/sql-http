import Foundation


public enum SQLValue: Codable, Equatable {
    
    case long(Int64)
    case string(String)
    case date(Date)
    case data(Data)
    case double(Double)
    case decimal(Decimal)
    case time(TimeInterval)
    case null
    
    public static func int(_ value: Int) -> SQLValue {
        return SQLValue.long(Int64(value))
    }
    
    public static func bool(_ value: Bool) -> SQLValue {
        return SQLValue.long(value ? 1 : 0)
    }
    
    public init(from decoder: Decoder) throws {
        let dataType = try decoder.singleValueContainer().decode(EnumerationWrapperDataType.self).dataType
        switch dataType {
        case "String":
            let value = try decoder.singleValueContainer().decode(EnumerationWrapper<String>.self).data!
            self = .string(value)
        case "Long":
            let value = try decoder.singleValueContainer().decode(EnumerationWrapper<Int64>.self).data!
            self = .long(value)
        case "Blob":
            let value = try decoder.singleValueContainer().decode(EnumerationWrapper<Data>.self).data!
            self = .data(value)
        case "Date":
            let value = try decoder.singleValueContainer().decode(EnumerationWrapper<Date>.self).data!
            self = .date(value)
        case "Double":
            let value = try decoder.singleValueContainer().decode(EnumerationWrapper<Double>.self).data!
            self = .double(value)
        case "Decimal":
            let value = try decoder.singleValueContainer().decode(EnumerationWrapper<String>.self).data!
            self = .decimal(NSDecimalNumber(string: value).decimalValue)
        case "Time":
            let value = try TimeInterval(decoder.singleValueContainer().decode(EnumerationWrapper<Int64>.self).data!) / TimeInterval(1000)
            self = .time(value)
        case "Null":
            self = .null
        default:
            throw UnableToDecodeError(dataType: dataType, targetType: "SQLValue")
        }
    }
    
    
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        switch self {
        case .long(let value): try container.encode(EnumerationWrapper(dataType: "Long", data: value))
        case .data(let value): try container.encode(EnumerationWrapper(dataType: "Blob", data: value))
        case .time(let value): try container.encode(EnumerationWrapper(dataType: "Time", data: Int64(value * 1000)))
        case .string(let value):
            try container.encode(EnumerationWrapper(dataType: "String", data: value))
        case .date(let value): try container.encode(EnumerationWrapper(dataType: "Date", data: value))
        case .double(let value): try container.encode(EnumerationWrapper(dataType: "Double", data: value))
        case .decimal(let value): try container.encode(EnumerationWrapper(dataType: "Decimal", data: NSDecimalNumber(decimal: value).stringValue))
        case .null:
            try container.encode(EnumerationWrapper<String>(dataType: "Null", data: nil))
        }
    }
    
}


struct EnumerationWrapper<T: Codable>: Codable {
    
    let dataType: String
    let data: T?
    
}

struct EnumerationWrapperDataType: Codable {
    
    let dataType: String
    
}

public struct UnableToDecodeError: Error {
    
    public let dataType: String
    public let targetType: String
    
}
