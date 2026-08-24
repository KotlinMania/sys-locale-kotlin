import Testing
import SysLocale

@Suite("SysLocale Swift Export Tests")
struct SysLocaleExportTests {
    @Test("Swift module imports and basic types are reachable")
    func swiftModuleLoads() throws {
        #expect(Bool(true))
    }
}
