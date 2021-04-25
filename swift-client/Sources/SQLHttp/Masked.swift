import Foundation


public struct Masked: Codable, Equatable {
    
    public let value: String
    
    public init(from decoder: Decoder) throws {
        value = try decoder.singleValueContainer().decode(String.self)
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(value)
    }
    
    public init(_ value: String) {
        self.value = value
    }
    
}


extension Masked: CustomStringConvertible {
    
    public var description: String {
        return "**********"
    }
    
    public var debugDescription: String {
      return "**********"
    }
    
}
