import 'package:event_bus/event_bus.dart';
/// @describe  
/// @author  XQ Yang
/// @date 12/26/2018  9:42 AM
///

final EventBus eventBus = new EventBus();
class OnTopImageChangeEvent{
    Map<String, dynamic> item;

    OnTopImageChangeEvent(this.item);
}
