// game.js

class Game {
    constructor() {
        this.gameId = null;
        this.boardSize = 5;
        this.board = document.getElementById('board');
        this.statusElement = document.getElementById('game-status');
        this.movesElement = document.getElementById('moves-count');
        this.startButton = document.getElementById('start-game');
        this.playerNameInput = document.getElementById('player-name');
        this.initializeEventListeners();
    }

    initializeEventListeners() {
        this.startButton.addEventListener('click', () => this.startNewGame());
    }

    createApiCall(url, options = {}) {
        return async () => {
            try {
                const response = await fetch(url, {
                    headers: { 'Accept': 'application/json' },
                    ...options
                });
                return await response.json();
            } catch (error) {
                console.error('API Error:', error);
                throw error;
            }
        };
    }

    async startNewGame() {
        try {
            this.removeGameOverDialogs();
            const apiCall = this.createApiCall(`/api/game/start?boardSize=${this.boardSize}`, {
                method: 'GET'
            });
            const gameState = await apiCall();
            this.gameId = gameState.gameId;
            this.renderBoard(gameState);
            this.updateStatus(gameState.status);
            this.updateMovesCount(gameState.movesCount || 0);
        } catch (error) {
            this.handleError('Error al iniciar el juego', error);
        }
    }

    removeGameOverDialogs() {
        const existingDialogs = document.querySelectorAll('.game-over');
        existingDialogs.forEach(dialog => dialog.remove());
    }

    async makeMove(q, r) {
        if (!this.gameId) return;
        try {
            const apiCall = this.createApiCall(
                `/api/game/block?gameId=${this.gameId}&q=${q}&r=${r}`,
                { method: 'POST' }
            );
            const gameState = await apiCall();
            this.renderBoard(gameState);
            this.updateStatus(gameState.status);
            this.updateMovesCount(gameState.movesCount || 0);

            // MODIFICACIÓN: Si el gato NO está en el tablero, el jugador pierde
            // Detectamos esto si en el estado del juego (gameState) hay una bandera especial,
            // o si la posición del gato no está en bounds (depende de backend).
            // Aquí asumimos que el backend ya devuelve el status PLAYER_LOST cuando esto ocurre,
            // pero si no, podemos detectarlo en el frontend así:
            if (!this.isCatInBounds(gameState.catPosition)) {
                this.updateStatus('PLAYER_LOST');
                this.showGameOver('PLAYER_LOST');
                return;
            }

            // También mostramos el game over si el backend ya lo indicó
            const gameEndStates = ['PLAYER_LOST', 'PLAYER_WON'];
            const isGameOver = gameEndStates.some(state => state === gameState.status);
            if (isGameOver) {
                this.showGameOver(gameState.status);
            }
        } catch (error) {
            this.handleError('Error al realizar el movimiento', error);
        }
    }

    // NUEVO: Determina si el gato está en el tablero (hexágono axial)
    isCatInBounds(catPosition) {
        if (!catPosition) return false;
        const q = catPosition.q;
        const r = catPosition.r;
        const s = -q - r;
        const radius = this.boardSize;
        const max = Math.max(Math.abs(q), Math.abs(r), Math.abs(s));
        return max <= radius;
    }

    updateMovesCount(count) {
        this.movesElement.textContent = count;
    }

    renderBoard(gameState) {
        this.board.innerHTML = '';
        const boardConfig = {
            hexSize: 25,
            centerX: 375,
            centerY: 375,
            cellWidth: 40,
            cellHeight: 46
        };
        const cells = this.generateCellPositions(boardConfig)
            .map(cell => this.createHexCell(cell, gameState, boardConfig));
        cells.forEach(cell => this.board.appendChild(cell));
    }

    generateCellPositions(config) {
        const positions = [];
        for (let q = -this.boardSize; q <= this.boardSize; q++) {
            for (let r = -this.boardSize; r <= this.boardSize; r++) {
                const s = -q - r;
                if (Math.abs(s) <= this.boardSize) {
                    const x = config.centerX + config.hexSize * (3/2 * q);
                    const y = config.centerY + config.hexSize * (Math.sqrt(3)/2 * q + Math.sqrt(3) * r);
                    const isBorder = Math.abs(q) === this.boardSize ||
                        Math.abs(r) === this.boardSize ||
                        Math.abs(s) === this.boardSize;
                    const type = isBorder ? 'border' : 'playable';
                    positions.push({ q, r, x, y, type });
                }
            }
        }
        return positions;
    }

    isValidHexPosition(q, r) {
        const s = -q - r;
        return Math.abs(q) < this.boardSize && Math.abs(r) < this.boardSize && Math.abs(s) < this.boardSize;
    }

    createHexCell(position, gameState, config) {
        const cell = document.createElement('div');
        if (position.type === 'border') {
            cell.className = 'hex-cell border-cell';
            cell.style.opacity = '0.3';
        } else {
            cell.className = 'hex-cell';
        }
        cell.style.left = `${position.x - config.cellWidth/2}px`;
        cell.style.top = `${position.y - config.cellHeight/2}px`;
        cell.setAttribute('data-q', position.q);
        cell.setAttribute('data-r', position.r);
        cell.setAttribute('data-type', position.type);

        const isCatPosition = this.isCatAt(position.q, position.r, gameState);
        const isBlocked = this.isCellBlocked(position.q, position.r, gameState);

        if (isCatPosition) {
            cell.classList.add('cat');
        } else if (isBlocked) {
            cell.classList.add('blocked');
        } else {
            const moveHandler = this.createMoveHandler(position.q, position.r);
            cell.addEventListener('click', moveHandler);
        }
        return cell;
    }

    createMoveHandler(q, r) {
        return () => this.makeMove(q, r);
    }

    isCatAt(q, r, gameState) {
        return q === gameState.catPosition.q && r === gameState.catPosition.r;
    }

    isCellBlocked(q, r, gameState) {
        return gameState.blockedCells.some(pos => pos.q === q && pos.r === r);
    }

    updateStatus(status) {
        const statusMessages = {
            'IN_PROGRESS': 'En progreso',
            'PLAYER_LOST': '¡El gato escapó!',
            'PLAYER_WON': '¡Atrapaste al gato!'
        };
        this.statusElement.textContent = statusMessages[status] || 'Estado desconocido';
    }

    showGameOver(status) {
        this.removeGameOverDialogs();
        const messages = {
            'PLAYER_LOST': '¡El gato escapó! Mejor suerte la próxima vez.',
            'PLAYER_WON': '¡Felicidades! ¡Atrapaste al gato!'
        };
        const message = messages[status] || 'Juego terminado';
        const gameOver = this.createGameOverDialog(message, status);
        document.body.appendChild(gameOver);
    }

    createGameOverDialog(message, status) {
        const gameOver = document.createElement('div');
        gameOver.className = 'game-over';
        const playerName = this.playerNameInput.value.trim() || 'Anónimo';
        gameOver.innerHTML = `
            <h2>${message}</h2>
            <p>Jugador: ${playerName}</p>
            <p>Movimientos: ${this.movesElement.textContent}</p>
            <div>
                <button onclick="saveScore('${this.gameId}', '${playerName}')">Guardar Puntuación</button>
                <button onclick="game.closeGameOverDialog()">Cerrar</button>
                <button onclick="game.startNewGame()">Nuevo Juego</button>
            </div>
        `;
        return gameOver;
    }

    closeGameOverDialog() {
        this.removeGameOverDialogs();
    }

    handleError(message, error) {
        console.error(message, error);
        alert(message);
    }
}

// Score logic (igual que antes)
const createScoreSaver = (gameId, playerName) => async () => {
    try {
        const response = await fetch(`/api/game/save-score?gameId=${gameId}&playerName=${encodeURIComponent(playerName)}`, {
            method: 'POST',
            headers: { 'Accept': 'application/json' }
        });
        if (response.ok) {
            alert('¡Puntuación guardada exitosamente!');
            return await response.json();
        } else {
            throw new Error('Error al guardar puntuación');
        }
    } catch (error) {
        console.error('Error saving score:', error);
        alert('Error al guardar la puntuación');
    }
};

async function saveScore(gameId, playerName) {
    const scoreSaver = createScoreSaver(gameId, playerName);
    await scoreSaver();
}

const createScoreFetcher = (endpoint) => async () => {
    try {
        const response = await fetch(endpoint, {
            headers: { 'Accept': 'application/json' }
        });
        return await response.json();
    } catch (error) {
        console.error('Error fetching scores:', error);
        return [];
    }
};

const scoreDisplayFunctions = {
    top: createScoreFetcher('/api/game/high-scores?limit=10'),
    winning: createScoreFetcher('/api/game/winning-scores'),
    recent: createScoreFetcher('/api/game/high-scores?limit=20')
};

const formatScore = (score) => {
    const winIcon = score.playerWon ? '🏆' : '❌';
    const calculatedScore = score.playerWon ?
        (1000 - score.movesCount * 10 + score.boardSize * 50 + Math.max(0, 300 - score.gameDurationSeconds)) :
        (100 - score.movesCount * 10);
    return {
        playerName: score.playerName,
        details: `${winIcon} ${score.movesCount} movimientos - Tablero ${score.boardSize}x${score.boardSize}`,
        score: Math.max(0, calculatedScore)
    };
};

const createScoreRenderer = (containerId) => (scores) => {
    const container = document.getElementById(containerId);
    if (!container) return;
    const scoreElements = scores
        .map(formatScore)
        .map(formattedScore => `
            <li>
                <div class="score-info">
                    <div class="player-name">${formattedScore.playerName}</div>
                    <div class="game-details">${formattedScore.details}</div>
                </div>
                <div class="score-value">${formattedScore.score}</div>
            </li>
        `);
    container.innerHTML = scoreElements.join('');
};

async function showHighScores() {
    const section = document.getElementById('high-score-section');
    section.style.display = 'block';
    await showScoreTab('top');
}

function hideHighScores() {
    const section = document.getElementById('high-score-section');
    section.style.display = 'none';
}

async function showScoreTab(tabType) {
    document.querySelectorAll('.tab-button').forEach(btn => btn.classList.remove('active'));
    event?.target?.classList.add('active') ||
        document.querySelector(`[onclick="showScoreTab('${tabType}')"]`)?.classList.add('active');
    const scoreFetcher = scoreDisplayFunctions[tabType];
    const scoreRenderer = createScoreRenderer('score-list');
    if (scoreFetcher) {
        const scores = await scoreFetcher();
        scoreRenderer(scores);
    }
}

const initializeGame = () => {
    window.game = new Game();
    return window.game;
};

window.addEventListener('load', initializeGame);
