/// @describe
/// @author  XQ Yang
/// @date 12/21/2018  4:22 PM
///
class OBus {
  static final bus = new OBus();

  final Map<String, List<Function>> map = new Map();

  void register(String type, Function(Object) fun) {
    if (map[type] == null) {
      map[type] = new List();
    }
    map[type].add(fun);
  }

  bool unregister(String type, Function(Object) fun) {
    print('unregister $fun');
    return map[type].remove(fun);
  }

  void clear(String type) {
    map[type].clear();
  }

  void notify(String type, Object arg) {

    for (var value in map[type]) {
      value(arg);
      print('  getmore $value');
    }
  }

  static const String TYPE_LOAD_MORE = "type_load_more";
}
