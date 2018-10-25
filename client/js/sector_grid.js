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
                        <input id="${GRID_PREFIX}${i}${SECTOR_SIDES[side]}">
                        <label for="${GRID_PREFIX}${i}${SECTOR_SIDES[side]}">${i}${SECTOR_SIDES[side]}</label>    
                    </div>
            `;
            GRID_IDs.push(GRID_PREFIX + i + SECTOR_SIDES[side]);
        }
        if (i % 3 === 2) {
            gridDom += '</div>';
        }
    }

    gridHolder.innerHTML = gridDom;
}

window.onload = setupGrid;
