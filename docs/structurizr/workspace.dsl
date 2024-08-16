workspace {

  model {
    freelancer = person "Freelancer"

    fws = softwareSystem "Freelancer Web Services" {
      description "Provides web services for freelancers to manage their work, projects and customers"

      fwsApplication = container "FWS Application" {
        description "Provides web services for freelancers to manage their work, projects and customers"
        technology "Spring Boot"

        freelancerService = component "Freelancer Service" {
          description "Allows freelancers to manage their work"
          technology "Java Package"
        }

        projectService = component "Project Service" {
          description "Allows freelancers to manage their projects"
          technology "Java Package"
        }

        customerService = component "Customer Service" {
          description "Allows freelancers to manage their customers"
          technology "Java Package"
        }

        billingService = component "Billing Service" {
          description "Allows freelancers to manage their billing"
          technology "Java Package"
        }
      }

      database = container "Database" {
        description "Stores freelancers, projects, customers and billing data"
        technology "H2 Database"
        tags "Database"

        freelancers = component "Freelancers" {
          description "Stores freelancers data"
          technology "DB Schema"
        }

        projects = component "Projects" {
          description "Stores projects data"
          technology "DB Schema"
        }

        customers = component "Customers" {
          description "Stores customers data"
          technology "DB Schema"
        }

        billing = component "Billing" {
          description "Stores billing data"
          technology "DB Schema"
        }
      }

      freelancer -> freelancerService "Manages work using" "JSON/HTTPS"
      freelancer -> projectService "Manages projects using" "JSON/HTTPS"
      freelancer -> customerService "Manages customers using" "JSON/HTTPS"
      freelancer -> billingService "Manages billing using" "JSON/HTTPS"

      freelancerService -> freelancers "Reads from and writes to" "JDBC"
      projectService -> projects "Reads from and writes to" "JDBC"
      customerService -> customers "Reads from and writes to" "JDBC"
      billingService -> billing "Reads from and writes to" "JDBC"

      freelancerService -> projectService "Reads project data from" "" "internal"
      projectService -> customerService "Reads customer data from" "" "internal"
      billingService -> freelancerService "Gets freelancer work data from" "" "internal"
      billingService -> projectService "Reads project data from" "" "internal"
      billingService -> customerService "Reads customer data from" "" "internal"
    }
  }

  views {
    systemContext fws "SystemContext" {
      title "Freelancer Web Services - System Context"
      include *
    }

    container fws "Containers" {
      title "Freelancer Web Services - Containers"
      include *
    }

    component fwsApplication "Components" {
      title "Freelancer Web Services - Components"
      include freelancer
      include element.type==component
      exclude relationship.tag==internal
    }

    component fwsApplication "Services" {
      title "FWS Application - Services"
      include *
      exclude freelancer
      exclude database
      exclude relationship.tag!=internal
    }

    styles {
      element "Person" {
        shape Person
      }
      element "Database" {
        shape Cylinder
      }
    }
  }

}
