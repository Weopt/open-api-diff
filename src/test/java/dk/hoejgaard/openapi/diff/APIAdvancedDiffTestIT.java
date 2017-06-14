package dk.hoejgaard.openapi.diff;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dk.hoejgaard.openapi.diff.compare.OperationDiff;
import dk.hoejgaard.openapi.diff.compare.ResourceDiff;
import dk.hoejgaard.openapi.diff.model.Endpoint;
import dk.hoejgaard.openapi.diff.model.ScopedProperty;
import dk.hoejgaard.openapi.diff.criteria.Diff;
import dk.hoejgaard.openapi.diff.criteria.Maturity;
import dk.hoejgaard.openapi.diff.compare.PropertyChanges;
import dk.hoejgaard.openapi.diff.criteria.Versions;
import io.swagger.models.HttpMethod;
import org.junit.Test;


import static org.junit.Assert.*;

public class APIAdvancedDiffTestIT {
    private final String REFERENCE_API = "elaborate_example_v2b.json";
    private final String SUBJECT_API = "elaborate_example_v2c.json";

    @Test
    public void testAPIWithMissingNewAndChanged() {
        APIDiff api = new APIDiff(REFERENCE_API, SUBJECT_API, Diff.ALL, Maturity.FULL, Versions.DOUBLE);
        List<Endpoint> newEndpoints = api.getAddedEndpoints();
        List<Endpoint> missingEndpoints = api.getMissingEndpoints();
        List<ResourceDiff> changedEndPoints = api.getChangedResourceDiffs();
        assertTrue(!newEndpoints.isEmpty());
        assertTrue(!missingEndpoints.isEmpty());
        assertTrue(!changedEndPoints.isEmpty());
    }

    @Test
    public void testAPIVerifyMissing() {
        APIDiff api = new APIDiff(REFERENCE_API, SUBJECT_API, Diff.ALL, Maturity.FULL, Versions.SINGLE);
        List<Endpoint> missingEndpoints = api.getMissingEndpoints();
        assertTrue(!missingEndpoints.isEmpty());
        assertEquals(1, missingEndpoints.size());
        assertEquals("/account-events/{group}", missingEndpoints.get(0).getPathUrl());
    }

    @Test
    public void testAPIVerifyNew() {
        APIDiff api = new APIDiff(REFERENCE_API, SUBJECT_API, Diff.ALL, Maturity.FULL, Versions.SINGLE);
        List<Endpoint> newEndpoints = api.getAddedEndpoints();
        List<ResourceDiff> resourceDiffs = api.getAllDiffs();
        assertTrue(!newEndpoints.isEmpty());
        assertEquals(5, newEndpoints.size());

        assertEquals("/account-events-new-resource", newEndpoints.get(0).getPathUrl());
        ResourceDiff resourceDiff = getResourceDiffByPath(resourceDiffs, "/account-events-new-resource");
        assertNotNull(resourceDiff);
        assertEquals(1, resourceDiff.getNonCompliantOperations().size());
        OperationDiff od = resourceDiff.getNonCompliantOperations().get(HttpMethod.GET);
        assertEquals(1, od.getDesignFlaws().size());
        assertEquals("OPINIONATED: should contain: future.response.compliance.for::.account-events-new-resource:::non-optimal.response.setup", true,
            od.getDesignFlaws().containsKey("future.response.compliance.for::.account-events-new-resource:::non-optimal.response.setup"));
        List<String> values = od.getDesignFlaws().get("future.response.compliance.for::.account-events-new-resource:::non-optimal.response.setup");
        assertEquals("OPINIONATED: should contain: the following response codes:  " +
            "[200, 301, 307, 410, 415, 429, 500, 503, 505]  " +
            "were either missing or may not include required response headers and thus do not support a future proof API"
            ,
            true, values.contains("the following response codes:  " +
                    "[200, 301, 307, 410, 415, 429, 500, 503, 505]  " +
                    "were either missing or may not include required response headers and thus do not support a future proof API"));
        assertEquals("/virtual-accounts", newEndpoints.get(4).getPathUrl());
        resourceDiff = getResourceDiffByPath(resourceDiffs, "/virtual-accounts");
        assertNotNull(resourceDiff);
        assertEquals(1, resourceDiff.getNonCompliantOperations().size());
        od = resourceDiff.getNonCompliantOperations().get(HttpMethod.GET);
        assertEquals(1, od.getDesignFlaws().size());
        assertEquals("OPINIONATED: should contain: future.response.compliance.for::.virtual-accounts:::non-optimal.response.setup", true,
            od.getDesignFlaws().containsKey("future.response.compliance.for::.virtual-accounts:::non-optimal.response.setup"));
        values = od.getDesignFlaws().get("future.response.compliance.for::.virtual-accounts:::non-optimal.response.setup");
        assertEquals("OPINIONATED: should contain: the following response codes:  " +
                "[200, 301, 307, 410, 415, 429, 500, 503, 505]  " +
                " were either missing or may not include required response headers and thus do not support a future proof API",
            true, values.contains("the following response codes:  " +
                "[200, 301, 307, 410, 415, 429, 500, 503, 505] " +
                " were either missing or may not include required response headers and thus do not support a future proof API"));

        assertEquals("/virtual-accounts/{regNo}-{accountNo}", newEndpoints.get(2).getPathUrl());
        resourceDiff = getResourceDiffByPath(resourceDiffs, "/virtual-accounts/{regNo}-{accountNo}");
        assertNotNull(resourceDiff);
        assertEquals(2, resourceDiff.getNonCompliantOperations().size());
        od = resourceDiff.getNonCompliantOperations().get(HttpMethod.GET);
        assertEquals(1, od.getDesignFlaws().size());
        assertEquals("OPINIONATED: should contain: future.response.compliance.for::.virtual-accounts.{regNo}-{accountNo}:::non-optimal.response.setup", true,
            od.getDesignFlaws().containsKey("future.response.compliance.for::.virtual-accounts.{regNo}-{accountNo}:::non-optimal.response.setup"));
        values = od.getDesignFlaws().get("future.response.compliance.for::.virtual-accounts.{regNo}-{accountNo}:::non-optimal.response.setup");
        assertEquals("OPINIONATED: should contain: the following response codes:  " +
                "[200, 301, 307, 410, 415, 429, 500, 503, 505]" +
                " were either missing or may not include required response headers and thus do not support a future proof API",
            true, values.contains("the following response codes:  " +
                    "[200, 301, 304, 307, 401, 403, 404, 410, 412, 415, 429, 500, 503, 505]" +
                    "  were either missing or may not include required response headers and thus do not support a future proof API"));

        assertEquals("/virtual-accounts/{regNo}-{accountNo}", newEndpoints.get(3).getPathUrl());
        resourceDiff = getResourceDiffByPath(resourceDiffs, "/virtual-accounts/{regNo}-{accountNo}");
        assertNotNull(resourceDiff);
        assertEquals(2, resourceDiff.getNonCompliantOperations().size());
        od = resourceDiff.getNonCompliantOperations().get(HttpMethod.PUT);
        Map<String, List<String>> flaws = od.getDesignFlaws();
        assertEquals(1, flaws.size());
        assertEquals("OPINIONATED: should contain: future.response.compliance.for::.virtual-accounts.{regNo}-{accountNo}:::non-optimal.response.setup", true,
            od.getDesignFlaws().containsKey("future.response.compliance.for::.virtual-accounts.{regNo}-{accountNo}:::non-optimal.response.setup"));
        assertEquals(true, getResourceDiffByValue(flaws, "the following response codes:  " +
            "[201, 415, 429, 500, 503] " +
            " were either missing or may not include required response headers and thus do not support a future proof API"));
        values = od.getDesignFlaws().get("future.response.compliance.for::.virtual-accounts.{regNo}-{accountNo}:::non-optimal.response.setup");
        assertEquals("OPINIONATED: should contain: the following response codes:  " +
                "[201, 415, 429, 500, 503] " +
                " were either missing or may not include required response headers and thus do not support a future proof API",
            true, values.contains("the following response codes:  " +
                "[201, 415, 429, 500, 503] " +
                " were either missing or may not include required response headers and thus do not support a future proof API"));
    }

    @Test
    public void testAPICompliance4New() {
        APIDiff api = new APIDiff(REFERENCE_API, SUBJECT_API, Diff.ALL, Maturity.FULL, Versions.SINGLE);
        List<ResourceDiff> observables = api.getAllDiffs();
        assertTrue(!observables.isEmpty());
        assertEquals(13, observables.size());

        ResourceDiff resourceCommon = getResourceDiffByPath(observables, "/accounts/{regNo}-{accountNo}");
        assertNotNull(resourceCommon);

        resourceCommon = getResourceDiffByPath(observables, "/accounts");
        assertNotNull(resourceCommon);

        resourceCommon = getResourceDiffByPath(observables, "/account-events");
        assertNotNull(resourceCommon);

        resourceCommon = getResourceDiffByPath(observables, "/account-events/{category}/{id}");
        assertNotNull(resourceCommon);

        resourceCommon = getResourceDiffByPath(observables, "/accounts/{regNo}-{accountNo}/transactions");
        assertNotNull(resourceCommon);

        resourceCommon = getResourceDiffByPath(observables, "/accounts/{regNo}-{accountNo}/transactions/{id}");
        assertNotNull(resourceCommon);

        resourceCommon = getResourceDiffByPath(observables, "/accounts/{regNo}-{accountNo}/reconciled-transactions");
        assertNotNull(resourceCommon);

        resourceCommon = getResourceDiffByPath(observables, "/accounts/{regNo}-{accountNo}/reconciled-transactions/{id}");
        assertNotNull(resourceCommon);

        ResourceDiff virtualAccount = getResourceDiffByPath(observables, "/virtual-accounts");
        assertNotNull(virtualAccount);

        virtualAccount = getResourceDiffByPath(observables, "/virtual-accounts/{regNo}-{accountNo}");
        assertNotNull(virtualAccount);

        ResourceDiff resourceNew = getResourceDiffByPath(observables, "/account-events-new-resource");
        assertNotNull(resourceNew);

        Map<HttpMethod, OperationDiff> nonCompliant = resourceNew.getNonCompliantOperations();

        assertEquals(true, nonCompliant.size() > 0);
        OperationDiff verb = nonCompliant.get(HttpMethod.GET);
        assertNotNull(verb);

        Map<String, List<String>> flaws = verb.getDesignFlaws();
        assertEquals(1, flaws.size());
        assertEquals(true, getResourceDiffByValue(flaws, "the following response codes:  " +
            "[200, 301, 307, 410, 415, 429, 500, 503, 505] " +
            " were either missing or may not include required response headers and thus do not support a future proof API"));
        assertEquals(1, flaws.get("future.response.compliance.for::.account-events-new-resource:::non-optimal.response.setup").size());
        Map<String, List<String>> breaks = verb.getBreakingChanges();
        assertEquals(0, breaks.size());

        Map<HttpMethod, OperationDiff> changed = resourceNew.getChangedOperations();
        assertEquals(0, changed.size());

        ResourceDiff virtualAccounts = getResourceDiffByPath(observables, "/virtual-accounts");
        assertNotNull(virtualAccounts);

        nonCompliant = virtualAccounts.getNonCompliantOperations();

        assertEquals(true, nonCompliant.size() > 0);
        verb = nonCompliant.get(HttpMethod.GET);
        assertNotNull(verb);

        flaws = verb.getDesignFlaws();
        assertEquals(1, flaws.size());
        assertEquals(true, getResourceDiffByValue(flaws, "the following response codes:  " +
            "[200, 301, 307, 410, 415, 429, 500, 503, 505] " +
            " were either missing or may not include required response headers and thus do not support a future proof API"));
        assertEquals(1, flaws.get("future.response.compliance.for::.virtual-accounts:::non-optimal.response.setup").size());
        breaks = verb.getBreakingChanges();
        assertEquals(0, breaks.size());

        changed = virtualAccounts.getChangedOperations();
        assertEquals(0, changed.size());

        virtualAccount = getResourceDiffByPath(observables, "/virtual-accounts/{regNo}-{accountNo}");
        assertNotNull(virtualAccount);

        nonCompliant = virtualAccount.getNonCompliantOperations();

        assertEquals(true, nonCompliant.size() > 0);
        verb = nonCompliant.get(HttpMethod.GET);
        assertNotNull(verb);

        flaws = verb.getDesignFlaws();
        assertEquals(1, flaws.size());
        assertEquals(true, getResourceDiffByValue(flaws, "the following response codes:  " +
            "[200, 301, 304, 307, 401, 403, 404, 410, 412, 415, 429, 500, 503, 505] " +
            " were either missing or may not include required response headers and thus do not support a future proof API"));
        assertEquals(1, flaws.get("future.response.compliance.for::.virtual-accounts.{regNo}-{accountNo}:::non-optimal.response.setup").size());
        breaks = verb.getBreakingChanges();
        assertEquals(0, breaks.size());


        verb = nonCompliant.get(HttpMethod.PUT);
        assertNotNull(verb);

        flaws = verb.getDesignFlaws();
        assertEquals(1, flaws.size());
        assertEquals(true, getResourceDiffByValue(flaws, "the following response codes:  " +
            "[201, 415, 429, 500, 503] " +
            " were either missing or may not include required response headers and thus do not support a future proof API"));
        assertEquals(1, flaws.get("future.response.compliance.for::.virtual-accounts.{regNo}-{accountNo}:::non-optimal.response.setup").size());
        assertEquals(true, getResourceDiffByValue(flaws, "the following response codes:  " +
            "[201, 415, 429, 500, 503] " +
            " were either missing or may not include required response headers and thus do not support a future proof API"));

        breaks = verb.getBreakingChanges();
        assertEquals(0, breaks.size());

        changed = virtualAccount.getChangedOperations();
        assertEquals(0, changed.size());

    }


    @Test
    public void testAPICompliance4Changed() {
        APIDiff api = new APIDiff(REFERENCE_API, SUBJECT_API, Diff.ALL, Maturity.FULL, Versions.SINGLE);
        List<ResourceDiff> observables = api.getAllDiffs();
        List<ResourceDiff> changed = api.getChangedResourceDiffs();
        assertTrue(!observables.isEmpty());
        assertTrue(!changed.isEmpty());
        assertEquals(13, observables.size());
        assertEquals(9, changed.size());

        ResourceDiff newShouldNotBeThere = getResourceDiffByPath(changed, "/account-events/{group}");
        assertNull(newShouldNotBeThere);

        ResourceDiff newShouldBeThere = getResourceDiffByPath(observables, "/account-events/{category}");
        assertNotNull(newShouldBeThere);

        ResourceDiff missingShouldNotBeThere = getResourceDiffByPath(changed, "/account-events-new-resource");
        assertNull(missingShouldNotBeThere);

        missingShouldNotBeThere = getResourceDiffByPath(observables, "/account-events/{group}");
        assertNull(missingShouldNotBeThere);

        ResourceDiff changedShouldBeThere = getResourceDiffByPath(changed, "/accounts/{regNo}-{accountNo}");
        assertNotNull(changedShouldBeThere);

        changedShouldBeThere = getResourceDiffByPath(changed, "/accounts/{regNo}-{accountNo}/transactions");
        assertNotNull(changedShouldBeThere);

        changedShouldBeThere = getResourceDiffByPath(changed, "/accounts/{regNo}-{accountNo}/transactions/{id}");
        assertNotNull(changedShouldBeThere);

        changedShouldBeThere = getResourceDiffByPath(changed, "/accounts/{regNo}-{accountNo}/reconciled-transactions");
        assertNotNull(changedShouldBeThere);

        changedShouldBeThere = getResourceDiffByPath(changed, "/accounts/{regNo}-{accountNo}/reconciled-transactions/{id}");
        assertNotNull(changedShouldBeThere);

        newShouldNotBeThere = getResourceDiffByPath(changed, "/virtual-accounts");
        assertNull(newShouldNotBeThere);

        newShouldNotBeThere = getResourceDiffByPath(changed, "/virtual-accounts/{regNo}-{accountNo}");
        assertNull(newShouldNotBeThere);


        Map<HttpMethod, OperationDiff> nonCompliant = changedShouldBeThere.getNonCompliantOperations();
        assertEquals(true, nonCompliant.size() > 0);
        OperationDiff verb = nonCompliant.get(HttpMethod.GET);
        assertNotNull(verb);

        Map<String, List<String>> flaws = verb.getDesignFlaws();
        assertEquals(1, flaws.size());
        assertEquals(true, getResourceDiffByValue(flaws, "the following response codes:  " +
            "[200, 301, 307, 404, 410, 415, 429, 500, 503, 505] " +
            " were either missing or may not include required response headers and thus do not support a future proof API"));
        assertEquals(1, flaws.get("future::.accounts.{regNo}-{accountNo}.reconciled-transactions.{id}:::non-optimal.response.setup").size());

        Map<String, List<String>> breaks = verb.getBreakingChanges();
        assertEquals(0, breaks.size());

        Map<HttpMethod, OperationDiff> changedOperations = changedShouldBeThere.getChangedOperations();
        assertEquals(2, changedOperations.size());
        OperationDiff getDiff = changedOperations.get(HttpMethod.GET);

        assertEquals(0, getDiff.getAddedParameters().size());
        assertEquals(0, getDiff.getMissingParameters().size());
        assertEquals(0, getDiff.getChangedParameters().size());

        assertEquals(0, getDiff.getMissingContentTypes().size());
        assertEquals(0, getDiff.getAddedContentTypes().size());

        assertEquals(0, getDiff.getAddedProperties().size());
        assertEquals(0, getDiff.getMissingProperties().size());

        assertEquals(0, getDiff.getAddedResponses().size());
        assertEquals(10, getDiff.getChangedResponses().size());

        assertEquals(0, getDiff.getBreakingChanges().size());
        assertEquals(19, getDiff.getDesignFlaws().size());
        assertEquals(9, getDiff.getExistingFlaws().size());
        assertEquals(5, getDiff.getPotentiallyBreakingChanges().size());

        OperationDiff putDiff = changedOperations.get(HttpMethod.PUT);

        assertEquals(0, putDiff.getAddedParameters().size());
        assertEquals(0, putDiff.getMissingParameters().size());
        assertEquals(0, putDiff.getChangedParameters().size());

        assertEquals(0, putDiff.getMissingContentTypes().size());
        assertEquals(0, putDiff.getAddedContentTypes().size());

        assertEquals(0, putDiff.getAddedProperties().size());
        assertEquals(0, putDiff.getMissingProperties().size());

        assertEquals(0, putDiff.getAddedResponses().size());
        assertEquals(7, putDiff.getChangedResponses().size());

        assertEquals(0, putDiff.getChanges().size());
        assertEquals(0, putDiff.getBreakingChanges().size());
        assertEquals(14, putDiff.getDesignFlaws().size());
        assertEquals(7, putDiff.getExistingFlaws().size());
        assertEquals(4, putDiff.getPotentiallyBreakingChanges().size());

        changedShouldBeThere = getResourceDiffByPath(changed, "/accounts/{regNo}-{accountNo}/transactions/{id}");
        changedOperations = changedShouldBeThere.getChangedOperations();
        assertEquals(2, changedOperations.size());
        getDiff = changedOperations.get(HttpMethod.GET);

        assertEquals(0, getDiff.getAddedParameters().size());
        assertEquals(0, getDiff.getMissingParameters().size());
        assertEquals(0, getDiff.getChangedParameters().size());

        assertEquals(0, getDiff.getMissingContentTypes().size());
        assertEquals(2, getDiff.getAddedContentTypes().size());

        assertEquals(0, getDiff.getAddedProperties().size());
        assertEquals(0, getDiff.getMissingProperties().size());

        assertEquals(0, getDiff.getAddedResponses().size());
        assertEquals(9, getDiff.getChangedResponses().size());

        assertEquals(0, getDiff.getBreakingChanges().size());
        assertEquals(18, getDiff.getDesignFlaws().size());
        assertEquals(9, getDiff.getExistingFlaws().size());
        assertEquals(4, getDiff.getPotentiallyBreakingChanges().size());

        putDiff = changedOperations.get(HttpMethod.PUT);

        assertEquals(0, putDiff.getAddedParameters().size());
        assertEquals(0, putDiff.getMissingParameters().size());
        assertEquals(1, putDiff.getChangedParameters().size());
        assertEquals(1, putDiff.getChangedParameters().get(0).getPotentiallyBreaking().size());
        assertNotNull(putDiff.getChangedParameters().get(0).getPotentiallyBreaking().get("header.Accept"));
        assertEquals(true, putDiff.getChangedParameters().get(0).getPotentiallyBreaking().get("header.Accept").get(0).contains("pattern.changed"));

        assertEquals(0, putDiff.getMissingContentTypes().size());
        assertEquals(0, putDiff.getAddedContentTypes().size());

        assertEquals(1, putDiff.getAddedProperties().size());
        assertEquals(1, putDiff.getChangedProperties().size());
        List<PropertyChanges> properties = putDiff.getChangedProperties();
        assertEquals(2, properties.get(0).getChanges().size());
        assertEquals(1, properties.get(0).getChanges().get("TransactionUpdate.body.description").size());
        assertNotNull(properties.get(0).getChanges().get("TransactionUpdate.body.description"));
        assertEquals(1, properties.get(0).getChanges().get("TransactionUpdate.body.amount").size());
        assertNotNull(properties.get(0).getChanges().get("TransactionUpdate.body.amount"));
        assertEquals(0, putDiff.getMissingProperties().size());

        assertEquals(0, putDiff.getAddedResponses().size());
        assertEquals(8, putDiff.getChangedResponses().size());

        assertEquals(0, putDiff.getChanges().size());
        assertEquals(2, putDiff.getBreakingChanges().size());
        assertEquals(15, putDiff.getDesignFlaws().size());
        assertEquals(7, putDiff.getExistingFlaws().size());
        assertEquals(8, putDiff.getPotentiallyBreakingChanges().size());
    }

    @Test
    public void testAPIVerifyChanged() {
        APIDiff api = new APIDiff(REFERENCE_API, SUBJECT_API, Diff.ALL, Maturity.FULL, Versions.DOUBLE);
        List<ResourceDiff> changedEndPoints = api.getChangedResourceDiffs();
        assertTrue(!changedEndPoints.isEmpty());
        assertEquals(9, changedEndPoints.size());

        ResourceDiff accounts = getResourceDiffByPath(changedEndPoints, "/accounts");
        assertNotNull(accounts);
        assertNotNull(accounts.getChangedOperations());
        assertEquals(1, accounts.getChangedOperations().size());
        OperationDiff od = accounts.getChangedOperations().get(HttpMethod.GET);
        assertNotNull(od);
        assertEquals(0, od.getAddedParameters().size());
        assertEquals(0, od.getMissingParameters().size());
        assertEquals(0, od.getAddedContentTypes().size());
        assertEquals(0, od.getMissingContentTypes().size());
        assertEquals(0, od.getMissingResponses().size());
        assertEquals(0, od.getAddedProperties().size());
        assertEquals(0, od.getMissingProperties().size());
        assertEquals("OPINIONATED: Missing response codes in existing API - considered forcing API change", 9, od.getChangedResponses().size());

        ResourceDiff account = getResourceDiffByPath(changedEndPoints, "/accounts/{regNo}-{accountNo}");
        assertNotNull(account);
        assertNotNull(account.getChangedOperations());
        assertEquals(2, account.getChangedOperations().size());
        od = account.getChangedOperations().get(HttpMethod.GET);
        assertNotNull(od);
        assertEquals(0, od.getAddedParameters().size());
        assertEquals(0, od.getMissingParameters().size());
        assertEquals(0, od.getAddedContentTypes().size());
        assertEquals(0, od.getMissingContentTypes().size());
        assertEquals(0, od.getMissingResponses().size());
        assertEquals(0, od.getAddedProperties().size());
        assertEquals(0, od.getMissingProperties().size());
        assertEquals("OPINIONATED: Missing response codes in existing API - considered forcing API change", 14, od.getChangedResponses().size());


        ResourceDiff transactions = getResourceDiffByPath(changedEndPoints, "/accounts/{regNo}-{accountNo}/transactions");
        assertNotNull(transactions);
        assertNotNull(transactions.getChangedOperations());
        assertEquals(1, transactions.getChangedOperations().size());
        od = transactions.getChangedOperations().get(HttpMethod.GET);
        assertNotNull(od);
        assertEquals(0, od.getAddedParameters().size());
        assertEquals(0, od.getMissingParameters().size());
        assertEquals(0, od.getAddedContentTypes().size());
        assertEquals(0, od.getMissingContentTypes().size());
        assertEquals(0, od.getMissingResponses().size());
        assertEquals(0, od.getAddedProperties().size());
        assertEquals(0, od.getMissingProperties().size());
        assertEquals("OPINIONATED: Missing response codes in existing API - considered forcing API change", 8, od.getChangedResponses().size());

        ResourceDiff transaction = getResourceDiffByPath(changedEndPoints, "/accounts/{regNo}-{accountNo}/transactions/{id}");
        assertNotNull(transaction);
        assertNotNull(transaction.getChangedOperations());
        assertEquals(2, transaction.getChangedOperations().size());
        od = transaction.getChangedOperations().get(HttpMethod.GET);
        assertNotNull(od);
        assertEquals(0, od.getAddedParameters().size());
        assertEquals(0, od.getMissingParameters().size());
        assertEquals(2, od.getAddedContentTypes().size());
        assertEquals(0, od.getMissingContentTypes().size());
        assertEquals(0, od.getMissingResponses().size());
        assertEquals(0, od.getAddedProperties().size());
        assertEquals(0, od.getMissingProperties().size());

        od = transaction.getChangedOperations().get(HttpMethod.PUT);
        assertNotNull(od);
        assertEquals(0, od.getAddedParameters().size());
        assertEquals(0, od.getMissingParameters().size());
        assertEquals(0, od.getAddedContentTypes().size());
        assertEquals(0, od.getMissingContentTypes().size());
        assertEquals(0, od.getMissingResponses().size());
        assertEquals(0, od.getMissingProperties().size());
        assertEquals("OPINIONATED: Missing response codes in existing API - considered forcing API change", 8, od.getChangedResponses().size());
        assertEquals(1, od.getAddedProperties().size());
        PropertyChanges pa = od.getChangedProperties().get(0);
        assertNotNull(pa);
        List<ScopedProperty>  ps = od.getChangedProperties().get(0).getAddedProperties();
        assertEquals("body.currency", ps.get(0).getEl());
        assertEquals("DKK", ps.get(0).getProperty().getExample());
        assertEquals("string", ps.get(0).getProperty().getType());
        assertEquals("the currency used in the transaction.", ps.get(0).getProperty().getDescription());
        assertEquals(false, ps.get(0).getProperty().getRequired());
    }

    private ResourceDiff getResourceDiffByPath(List<ResourceDiff> resourceDiffs, String path) {
        for (ResourceDiff resource : resourceDiffs) {
            if (resource.getPathUrl().equals(path)) {
                return resource;
            }
        }
        return null;
    }

    private boolean getResourceDiffByValue(Map<String, List<String>>  resource, String value) {
        List<String> found;
        for (String key : resource.keySet()) {
            found = resource.get(key).stream()
                .filter(k -> k.contains(value))
                .collect(Collectors.toList());
            if (found.size() > 0) {
                return true;
            }
        }
        return false;
    }

}