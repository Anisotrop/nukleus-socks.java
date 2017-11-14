/**
 * Copyright 2016-2017 The Reaktivity Project
 *
 * The Reaktivity Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.reaktivity.nukleus.socks.internal.stream.types;

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.nukleus.socks.internal.types.Flyweight;

public class SocksNegotiationResponseFW extends FragmentedFlyweight<SocksNegotiationResponseFW>
{
    /* From https://tools.ietf.org/html/rfc1928

       The server selects from one of the methods given in METHODS, and
       sends a METHOD selection message:
                         +----+--------+
                         |VER | METHOD |
                         +----+--------+
                         | 1  |   1    |
                         +----+--------+
     */

    private static final int FIELD_OFFSET_VERSION = 0;
    private static final int FIELD_SIZEBY_VERSION = BitUtil.SIZE_OF_BYTE;

    private static final int FIELD_OFFSET_METHOD = FIELD_OFFSET_VERSION + FIELD_SIZEBY_VERSION;
    private static final int FIELD_SIZEBY_METHOD = BitUtil.SIZE_OF_BYTE;

    private BuildState buildState = BuildState.INITIAL;

    @Override
    public int limit()
    {
        return decodeLimit(buffer(), offset());
    }

    @Override
    public ReadState canWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        final int maxLength = maxLimit - offset;
        if (maxLength < 2 ||
            decodeLimit(buffer, offset) > maxLimit)
        {
            return ReadState.INCOMPLETE;
        }
        return ReadState.FULL;
    }

    @Override
    public BuildState getBuildState()
    {
        return buildState;
    }

    private int decodeLimit(
        DirectBuffer buffer,
        int offset)
    {
        return offset + FIELD_OFFSET_METHOD + FIELD_SIZEBY_METHOD;
    }

    public byte version()
    {
        return (buffer().getByte(offset() + FIELD_OFFSET_VERSION));
    }

    public byte method()
    {
        return (buffer().getByte(offset() + FIELD_OFFSET_METHOD));
    }

    public static final class Builder extends Flyweight.Builder<SocksNegotiationResponseFW>
    {
        public Builder()
        {
            super(new SocksNegotiationResponseFW());
        }

        @Override
        public SocksNegotiationResponseFW build()
        {
            flyweight().buildState = BuildState.FINAL;
            return super.build();
        }

        @Override
        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            flyweight().buildState = BuildState.INITIAL;
            super.wrap(buffer, offset, maxLimit);
            return this;
        }

        public Builder version(byte version)
        {
            int newLimit = limit() + BitUtil.SIZE_OF_BYTE;
            checkLimit(newLimit, maxLimit());
            buffer().putByte(offset() + FIELD_OFFSET_VERSION, version);
            limit(newLimit);
            return this;
        }

        public Builder method(byte method)
        {
            int newLimit = limit() + BitUtil.SIZE_OF_BYTE;
            checkLimit(newLimit, maxLimit());
            buffer().putByte(offset() + FIELD_OFFSET_METHOD, method);
            limit(newLimit);
            return this;
        }
    }
}
