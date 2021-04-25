import Foundation


public struct Column: Codable {
    
    public let index: Int
    public let name: String
    public let label: String
    public let value: SQLValue
    
    public init(index: Int, name: String, label: String, value: SQLValue) {
        self.index = index
        self.name = name
        self.label = label
        self.value = value
    }
    
}


public extension Column {
    
    var stringOpt: String? {
        switch value {
        case .string(let value): return value
        case .long(let value): return String(value)
        default: return nil
        }
    }
    
    var string: String {
        return stringOpt!
    }
    
    var intOpt: Int? {
        switch value {
        case .long(let value): return Int(value)
        default: return nil
        }
    }
    
    var int: Int {
        return intOpt!
    }
    
    var longOpt: Int64? {
        switch value {
        case .long(let value): return value
        default: return nil
        }
    }

    var long: Int64 {
        return longOpt!
    }
    
    var boolOpt: Bool? {
        switch value {
        case .long(let value): return value != 0
        case .string(let value) where value.lowercased() == "0": return false
        case .string(let value) where value.lowercased() == "1": return true
        case .string(let value) where value.lowercased() == "true": return true
        case .string(let value) where value.lowercased() == "false": return false
        case .string(let value) where value.lowercased() == "t": return true
        case .string(let value) where value.lowercased() == "f": return false
        case .string(let value) where value.lowercased() == "": return false
        default: return nil
        }
    }
    
    var bool: Bool {
        return boolOpt!
    }
    
    var dataOpt: Data? {
        switch value {
        case .data(let value): return value
        case .string(let value): return value.data(using: .utf8)
        default: return nil
        }
    }
    
    var data: Data {
        return dataOpt!
    }
    
    var dateOpt: Date? {
        switch value {
        case .date(let value): return value
        default: return nil
        }
    }
    
    var date: Date {
        return dateOpt!
    }
    
    var localDateOpt: Date? {
        switch value {
        case .date(let value):
            var components = SQLHttpClient.calendar.dateComponents(
                [.era, .year, .month, .day, .hour, .minute, .second, .nanosecond],
                from: value
            )
            components.timeZone = TimeZone.current
            return SQLHttpClient.calendar.date(from: components)
        default: return nil
        }
    }
    
    var localDate: Date {
        return localDateOpt!
    }
    
    var isNull: Bool {
        switch value {
        case .null: return true
        default: return false
        }
    }
    
}
