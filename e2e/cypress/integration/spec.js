it("Show app", () => {
    cy.visit("/")
    cy.title().should('eq', 'Registry viewer')
})

it("Connect to a Docker registry", () => {
    cy.visit("/")

    cy.title().should('eq', 'Registry viewer')
    cy.contains("Connect to Docker registry")

    cy.get("#connect_form")
    cy.get("#url").type("http://registry:5000")
    cy.get("#insecure1")
    cy.get("#use_authentication_button")
    cy.get("input[type=submit]").click()

    cy.contains("Connected to registry")
    cy.contains("Test connection").click()
    cy.contains("Successfully connected!")
})

it("Catalog test", () => {
    cy.visit("/")

    cy.title().should('eq', 'Registry viewer')
    cy.contains("Connect to Docker registry")

    cy.get("#connect_form")
    cy.get("#url").type("http://registry:5000")
    cy.get("#insecure1")
    cy.get("#use_authentication_button")
    cy.get("input[type=submit]").click()

    cy.contains("Catalog").click()
    cy.contains("alpine")
    cy.contains("busybox")
    cy.contains("registry").parent().contains("View tags").click()
    cy.contains("latest").parent().contains("Manifest").click()

    cy.contains("Manifest detail")
    cy.contains("Manifest")
    cy.contains("Manifest config")
    cy.contains("Layers")
})

it("Disconnect test", () => {
    cy.visit("/")

    cy.title().should('eq', 'Registry viewer')
    cy.contains("Connect to Docker registry")

    cy.get("#connect_form")
    cy.get("#url").type("http://registry:5000")
    cy.get("#insecure1")
    cy.get("#use_authentication_button")
    cy.get("input[type=submit]").click()

    cy.contains("Connected to registry")
    cy.contains("Disconnect").click()
    cy.get("div[role=dialog]").get("div[class=ui-dialog-buttonset]").contains("Disconnect").click()

    cy.get("#connect_form")
})
