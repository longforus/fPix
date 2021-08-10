


import 'package:flutter/foundation.dart';
import 'package:sentry/sentry.dart';

final SentryClient _sentry = SentryClient(SentryOptions(dsn: "https://0dbed905ee3145d8853dfdb4975adf89:ee1132c8a05a4b01b8f99c7c1f058427@sentry.io/1551105"));

bool get isInDebugMode {
    // Assume you're in production mode.
    bool inDebugMode = !kReleaseMode;

    // Assert expressions are only evaluated during development. They are ignored
    // in production. Therefore, this code only sets `inDebugMode` to true
    // in a development environment.
//    assert(inDebugMode = true);

    return inDebugMode;
}





Future<void> reportError(dynamic error, dynamic stackTrace) async {
    // Print the exception to the console.
    print('Caught error: $error');
    if (isInDebugMode) {
        // Print the full stacktrace in debug mode.
        print(stackTrace);
        return;
    } else {
        // Send the Exception and Stacktrace to Sentry in Production mode.
        _sentry.captureException(
            error,
            stackTrace: stackTrace,
        );
    }
}


