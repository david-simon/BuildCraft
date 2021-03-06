package buildcraft.transport.statements;

import java.util.Collection;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.api.gates.IGate;
import buildcraft.api.statements.*;
import buildcraft.api.transport.pipe.IPipeHolder;
import buildcraft.api.transport.pipe.PipeEventStatement;
import buildcraft.api.transport.pluggable.PipePluggable;

import buildcraft.lib.misc.ColourUtil;
import buildcraft.transport.BCTransportStatements;
import buildcraft.transport.plug.PluggablePulsar;
import buildcraft.transport.wire.IWireEmitter;

import javax.annotation.Nonnull;

public enum TransportActionProvider implements IActionProvider {
    INSTANCE;

    @Override
    public void addInternalActions(Collection<IActionInternal> actions, IStatementContainer container) {
        if (container instanceof IGate) {
            IGate gate = (IGate) container;
            IPipeHolder holder = gate.getPipeHolder();
            holder.fireEvent(new PipeEventStatement.AddActionInternal(holder, actions));

            if (container instanceof IWireEmitter) {
                for (EnumDyeColor colour : ColourUtil.COLOURS) {
                    if (TriggerPipeSignal.doesGateHaveColour(gate, colour)) {
                        actions.add(BCTransportStatements.ACTION_PIPE_SIGNAL[colour.ordinal()]);
                    }
                }
            }
        }
    }

    @Override
    public void addInternalSidedActions(Collection<IActionInternalSided> actions, IStatementContainer container, @Nonnull EnumFacing side) {
        if (container instanceof IGate) {
            IGate gate = (IGate) container;
            IPipeHolder holder = gate.getPipeHolder();
            holder.fireEvent(new PipeEventStatement.AddActionInternalSided(holder, actions, side));
            PipePluggable plug = holder.getPluggable(side);
            if (plug instanceof PluggablePulsar) {
                actions.add(BCTransportStatements.ACTION_PULSAR_CONSTANT);
                actions.add(BCTransportStatements.ACTION_PULSAR_SINGLE);
            }
        }
    }

    @Override
    public void addExternalActions(Collection<IActionExternal> actions, @Nonnull EnumFacing side, TileEntity tile) {

    }
}
