package cn.flood.cloud.grpc.sentinel.slot;

import cn.flood.cloud.grpc.sentinel.slot.flow.FlowEarlyWarningSlot;
import com.alibaba.csp.sentinel.slotchain.ProcessorSlotChain;
import com.alibaba.csp.sentinel.slotchain.SlotChainBuilder;
import com.alibaba.csp.sentinel.slots.DefaultSlotChainBuilder;

public class CustomSlotChainBuilder implements SlotChainBuilder {


  @Override
  public ProcessorSlotChain build() {
    ProcessorSlotChain chain = new DefaultSlotChainBuilder().build();
    chain.addLast(new FlowEarlyWarningSlot());
//        chain.addLast(new DegradeEarlyWarningSlot());
    return chain;
  }
}