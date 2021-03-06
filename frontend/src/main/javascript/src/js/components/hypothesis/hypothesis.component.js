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

import * as d3 from 'd3';
import {dagre, graphlib, render as Renderer} from 'dagre-d3';
import forEach from 'lodash/forEach';
import {events} from '../../constants';

// various styles used to style the hypothesis
const STYLE = {
    edge: 'stroke: rgba(0, 0, 0, 0.3); stroke-width: 3; fill:none',
    edgeLabel: 'display: inline; font-weight: bold; line-height: 1; text-align: center; white-space: nowrap; vertical-align: baseline; font-size: 10px',
    nodeLabel: 'display: inline; font-weight: bold; line-height: 1; text-align: center; white-space: nowrap; vertical-align: baseline; font-size: 12px',
    node: 'fill: #fff; stroke: #000; stroke-width: 1',
    initNode: 'fill: #B3E6B3; stroke: #5cb85c; stroke-width: 3',
    svg: {
        'font-family': '"Helvetica Neue",Helvetica,Arial,sans-serif',
        'font-size': '12px',
        'line-height': '1.42857',
        'color': '#333'
    }
};

/**
 * The component that is used to display hypotheses.
 *
 * Attribute 'isSelectable' should only be true if it should be possible to select input output pairs from the
 * hypothesis.
 *
 * Attribute 'layoutSettings' is optional.
 *
 * Use: <hypothesis data="..." is-selectable="true|false" layout-settings="..."></hypothesis>.
 */
class HypothesisComponent {

    /**
     * Constructor.
     *
     * @param $scope
     * @param $element
     * @param {EventBus} EventBus
     */
    // @ngInject
    constructor($scope, $element, EventBus) {
        this.$scope = $scope;
        this.$element = $element;
        this.EventBus = EventBus;

        this.renderer = new Renderer();
        this.graph = null;

        this.svg = d3.select($element.find('svg')[0]);
        this.svgGroup = this.svg.select('g');
        this.svgContainer = $element.find('svg')[0].parentNode;

        this.resizeHandler = this.fitSize.bind(this);

        this.svg.style(STYLE.svg);

        $scope.$on('$destroy', () => {
            window.removeEventListener('resize', this.resizeHandler);
        });

        // do this whole stuff so that the size of the svg adjusts to the window
        window.addEventListener('resize', this.resizeHandler);
    }

    $onInit() {
        this.$scope.$watch('vm.data', data => {
            if (data) {
                this.data = data;
                this.init();
            }
        });

        this.$scope.$watch('vm.layoutSettings', settings => {
            if (settings) {
                this.layoutSettings = settings;
                this.init();
            }
        });
    }

    init() {
        this.fitSize();
        this.layout();
        this.render();
        this.handleEvents();
    }

    /**
     * Adjust the size of the svg to the size of the visible container.
     */
    fitSize() {
        this.svg.attr('width', this.svgContainer.clientWidth);
        this.svg.attr('height', this.svgContainer.clientHeight);
    }

    /**
     * Layout the graph.
     */
    layout() {
        this.graph = new graphlib.Graph({
            directed: true,
            multigraph: true
        });

        if (this.layoutSettings !== null) {
            this.graph.setGraph({
                edgesep: this.layoutSettings.edgesep,
                nodesep: this.layoutSettings.nodesep,
                ranksep: this.layoutSettings.ranksep
            });
        } else {
            this.graph.setGraph({
                edgesep: 25
            });
        }

        // add nodes to the graph
        this.data.nodes.forEach(node => {
            this.graph.setNode(node.toString(), {
                shape: 'circle',
                label: node.toString(),
                width: 25,
                labelStyle: STYLE.nodeLabel,
                style: node === this.data.initNode ? STYLE.initNode : STYLE.node
            });
        });

        // another format of a graph for merged multi edges
        // graph = {<from>: {<to>: <label[]>, ...}, ...}
        const graph = {};

        // build data structure for the alternative representation by
        // pushing some data
        this.data.edges.forEach(edge => {
            if (!graph[edge.from]) {
                graph[edge.from] = {};
                graph[edge.from][edge.to] = [edge.input + ' / ' + edge.output];
            } else {
                if (!graph[edge.from][edge.to]) {
                    graph[edge.from][edge.to] = [edge.input + ' / ' + edge.output];
                } else {
                    graph[edge.from][edge.to].push(edge.input + ' / ' + edge.output);
                }
            }
        });

        // add edges to the rendered graph and combine <label[]>
        forEach(graph, (k, from) => {
            forEach(k, (labels, to) => {
                this.graph.setEdge(from, to, {
                    label: labels.join('\n'),
                    labeloffset: 5,
                    style: STYLE.edge,
                    labelStyle: STYLE.edgeLabel,
                    curve: d3.curveBasis
                }, (from + '' + to));
            });
        });

        // layout with dagre
        dagre.layout(this.graph, {});
    }

    /**
     * Render the graph to the svg.
     */
    render() {
        // clear the svg so that there aren't rendered multiple hypotheses
        this.svgGroup.html('');

        // Run the renderer. This is what draws the final graph.
        this.renderer(this.svgGroup, this.graph);

        // make that arrow heads are displayed in the exported svg
        this.svg.selectAll('.path').nodes().forEach((path) => {
            const markerId = '#' + path.getAttribute('marker-end').split(')')[0].split('#')[1];
            path.setAttribute('marker-end', `url(${markerId})`);
        });

        // in order to prevent only a white screen in some browsers, firing a resize event on the window
        // displays the svg contents
        window.setTimeout(() => {
            window.dispatchEvent(new Event('resize'));
        }, 100);
    }

    /**
     * Create click and zoom events.
     */
    handleEvents() {
        const $scope = this.$scope;
        const EventBus = this.EventBus;

        // zoom support
        const zoom = d3.zoom().on('zoom', () => {
            this.svgGroup.attr('transform', d3.event.transform);
        });
        zoom.scaleExtent([0.5, 3]);
        this.svg.call(zoom);

        // Center the graph
        const offsetX = (this.svg.attr('width') - this.graph.graph().width) / 2;
        this.svg.call(zoom.transform, d3.zoomIdentity.translate(offsetX, 20));

        // attach click events for the selection of counter examples to the edge labels
        // only if counterexamples is defined
        if (this.isSelectable) {
            this.svg.selectAll('.edgeLabel tspan').on('click', function () {
                const label = this.innerHTML.split('/'); // separate name from output
                $scope.$apply(() => {
                    EventBus.emit(events.HYPOTHESIS_LABEL_SELECTED, {
                        input: label[0].trim(),
                        output: label[1].trim()
                    });
                });
            });
        }
    }
}

export const hypothesisComponent = {
    template: require('./hypothesis.component.html'),
    bindings: {
        data: '=',
        layoutSettings: '=',
        isSelectable: '@'
    },
    controller: HypothesisComponent,
    controllerAs: 'vm'
};
