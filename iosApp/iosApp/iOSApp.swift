import SwiftUI

import Shared

@main
struct iOSApp: App {
    init() {
        IOSKoinInitializer().initialize()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
