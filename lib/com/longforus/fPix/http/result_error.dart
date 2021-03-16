class ResultError implements Exception {
  int code;
  final String message;

  ResultError(this.code, this.message);

  @override
  String toString() {
    return "onError code=$code  message=$message";
  }
}
