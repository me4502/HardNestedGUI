const gridHolder = document.querySelector("#grid-holder");

const SECTOR_COUNT = 16;
const SECTOR_SIDES = ['A', 'B'];

export const GRID_IDs = [];
export const GRID_PREFIX = 'grid-';

function setupGrid() {
    let gridDom = '';

    for (let i = 0; i < SECTOR_COUNT; i++) {
        if (i % 3 === 0) {
            gridDom += '<div class="row">';
        }
        for (let side in SECTOR_SIDES) {
            gridDom += `
                    <div class="col s2">
                        <input id="${GRID_PREFIX}${SECTOR_SIDES[side]}${i}" class="grid-text">
                        <label for="${GRID_PREFIX}${SECTOR_SIDES[side]}${i}">${SECTOR_SIDES[side]}${i}</label>    
                    </div>
            `;
            GRID_IDs.push(GRID_PREFIX + SECTOR_SIDES[side] + i);
        }
        if (i % 3 === 2) {
            gridDom += '</div>';
        }
    }

    gridHolder.innerHTML = gridDom;
}

export function setGridKey(grid, key) {
    document.getElementById(GRID_PREFIX + grid).value = key;
}

export function getGridKeys() {
    let keys = {};
    for (let id in GRID_IDs) {
        let idName = GRID_IDs[id].replace(GRID_PREFIX, '');
        keys[idName] = document.getElementById(GRID_IDs[id]).value;
    }
    return keys;
}

window.onload = setupGrid;
