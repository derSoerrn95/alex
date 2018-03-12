/*
 * Copyright 2018 TU Dortmund
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.learnlib.alex.testing.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.learnlib.alex.data.entities.ExecuteResult;
import de.learnlib.alex.data.entities.Symbol;
import de.learnlib.alex.data.entities.SymbolRepresentation;
import de.learnlib.alex.learning.services.connectors.ConnectorManager;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

/** The test case step that executes a symbol. */
@Entity
@JsonTypeName("symbol")
public class TestCaseSymbolStep extends TestCaseStep {

    private static final long serialVersionUID = -2192813855439134078L;

    /** The symbol to execute. */
    @OneToOne(
            fetch = FetchType.EAGER
    )
    @JsonIgnore
    private Symbol symbol;

    @Override
    public ExecuteResult execute(ConnectorManager connectors) {
        return symbol.execute(connectors);
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    @JsonProperty("symbol")
    public SymbolRepresentation getSymbolId() {
        return new SymbolRepresentation(symbol);
    }

    @JsonProperty("symbol")
    public void setSymbolId(Long symbolId) {
        symbol = new Symbol();
        symbol.setId(symbolId);
    }
}